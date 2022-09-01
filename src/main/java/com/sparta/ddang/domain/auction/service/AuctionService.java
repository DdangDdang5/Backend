package com.sparta.ddang.domain.auction.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.ddang.domain.auction.dto.request.AuctionRequestDto;
import com.sparta.ddang.domain.auction.dto.request.AuctionUpdateRequestDto;
import com.sparta.ddang.domain.auction.dto.resposne.AuctionResponseDto;
import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.auction.repository.AuctionRepository;
import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.mulltiimg.awsS3exceptionhandler.FileTypeErrorException;
import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import com.sparta.ddang.domain.mulltiimg.repository.MultiImgRepository;
import com.sparta.ddang.domain.viewcnt.entity.ViewCnt;
import com.sparta.ddang.domain.viewcnt.repository.ViewCntRepository;
import com.sparta.ddang.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Service
public class AuctionService {

    private final AmazonS3Client amazonS3Client;

    private final MultiImgRepository multiImgRepository;

    private final AuctionRepository auctionRepository;

    private final TokenProvider tokenProvider;

    private final ViewCntRepository viewCntRepository;


    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    @Transactional
    public ResponseDto<?> getAllAuction() {

        return ResponseDto.success(auctionRepository.findAllByOrderByModifiedAtDesc());

    }

    // 시큐리티에서 상세페이지의 권한을 풀어줌
    // 일반 사용자는 조회수에 영향을 미치면 안됨
    // 회원들만 조회수에 영향을 미침
    // 회원들도 게시글당 1번만 영향을 미침.
    @Transactional
    public ResponseDto<?> getDetailAuction(Long auctionId, HttpServletRequest request) {

        Auction auction = checkAuction(auctionId);

        if (auction == null){

            return ResponseDto.fail("해당 경매 게시글이 없습니다.");

        }

        // 회원일경우 토큰값을 받아오니까 회원정보를 가져옴
        Member member = validateMember(request);

        // 일반인들에게는 토큰이 없음 대신 해당 상세페이지만 보여줌.
        if (null == member) {

            return ResponseDto.success(auction);

        }

        // 만약 댓글사용시 댓글 로직 추가.
        
        // 회원일경우 멤버 고유 번호를 가져옴
        Long memId = member.getId();
        
        // 해당 경매 게시글 번호를 가져옴
        Long aucId = auctionId;
        
        // 만약 해당 게시글에 방문한적이 있으면 그냥 해당 게시글만 보여줌
        if (viewCntRepository.existsByMemberIdAndAuctionId(memId,aucId)){

            return ResponseDto.success(auction);

        }
        // 처음 방문하면 ViewCnt테이블에 회원 정보와 해당 게시글 정보를 저장함.
        ViewCnt viewCnt = new ViewCnt(memId,aucId);

        viewCntRepository.save(viewCnt);

        // 해당 게시글의 카운트 수를 증가시킴 --> 해당 게시글을 처음 방문한 회원만 조회수를 증가시킴.
        auction.cntAuction();

        // 적용된 변경사항을 저장함.
        auctionRepository.save(auction);





//        ViewCnt viewCnt = new ViewCnt();
//
//        viewCnt.cntView(auctionId);
//
//        viewCntRepository.save(viewCnt);
//
//        //List<ViewCnt> optionalViewCnt = viewCntRepository.findByAuctionId(auctionId);
//        ViewCnt optionalViewCnt = viewCntRepository.findByAuctionId(auctionId);
//
//
//        Long viewCnt1 = optionalViewCnt.getViewCnt();
//
//        auction.cntAuction(viewCnt1);
//
//        auctionRepository.save(auction);


        return ResponseDto.success(auction);


    }




    @Transactional
    public ResponseDto<?> createAuction(List<MultipartFile> multipartFile,
                                        AuctionRequestDto auctionRequestDto,
                                        HttpServletRequest request) throws IOException {
        System.out.println("==================================================");
        System.out.println(auctionRequestDto.getTitle());
        System.out.println(auctionRequestDto.getProductName());
        System.out.println(auctionRequestDto.getContent());

        System.out.println(auctionRequestDto.getStartPrice());
        System.out.println(auctionRequestDto.getAuctionPeriod());

        System.out.println(auctionRequestDto.getCategory());
        System.out.println(auctionRequestDto.getRegion());

        System.out.println(auctionRequestDto.isDelivery());
        System.out.println(auctionRequestDto.isDirect());
        System.out.println("==================================================");



        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        //List<Auction> auctionList = auctionRepository.findAll();


        List<MultiImage> multiImages = new ArrayList<>();

        Auction auction = new Auction(multiImages,member,auctionRequestDto);

        auctionRepository.save(auction);

        List<String> imgUrlList = new ArrayList<>();

        for (MultipartFile imgFile : multipartFile) {
            String fileName = upload(imgFile);
            String imgUrl = URLDecoder.decode(fileName, "UTF-8");
            if (imgUrl.equals("false")) {
                return ResponseDto.fail("이미지 파일만 업로드 가능합니다.");
            }
            // 이미지 저장 및 이미지테이블이랑 포스트테이블매핑하기

            MultiImage multiImage = new MultiImage(imgUrl, auction.getId(), member.getId());

            multiImages.add(multiImage);

            imgUrlList.add(imgUrl);

            multiImgRepository.save(multiImage);

            System.out.println("========================");
            System.out.println(multiImages.get(0));

            System.out.println("========================");

        }

        //auction = new Auction(multiImages);

        return ResponseDto.success(
                AuctionResponseDto.builder()
                        .auctionId(auction.getId())
                        .productName(auction.getProductName())
                        .memberId(member.getId())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
                        .title(auction.getTitle())
                        .content(auction.getContent())
                        .multiImages(auction.getMultiImages())
                        .startPrice(auction.getStartPrice())
                        .nowPrice(auction.getNowPrice())
                        .auctionPeriod(auction.getAuctionPeriod())
                        .category(auction.getCategory())
                        .region(auction.getRegion())
                        .direct(auction.isDirect())
                        .delivery(auction.isDelivery())
                        .viewerCnt(auction.getViewerCnt())
                        .auctionStatus(true)
                        .participantStatus(auction.isParticipantStatus())
                        .favoriteStatus(auction.isFavoriteStatus())
                        .createdAt(auction.getCreatedAt())
                        .modifiedAt(auction.getModifiedAt())
                        .build()
        );

    }

    @Transactional
    public ResponseDto<?> updateAuction(List<MultipartFile> multipartFile,
                                        Long auctionId,
                                        AuctionUpdateRequestDto auctionUpdateRequestDto,
                                        HttpServletRequest request) throws IOException {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        Auction auction = checkAuction(auctionId);

        if (auction == null){

            return ResponseDto.fail("해당 경매 게시글이 없습니다.");

        }
        
        //수정시 해당 경매게시글에 있는 이미지 전체 삭제
        multiImgRepository.deleteAllByMemberIdAndAuctionId(member.getId(), auctionId);
        
        List<MultiImage> multiImages = new ArrayList<>();

        auction.updateAuction(multiImages,member,auctionUpdateRequestDto);

        auctionRepository.save(auction);

        List<String> imgUrlList = new ArrayList<>();

        for (MultipartFile imgFile : multipartFile) {
            String fileName = upload(imgFile);
            String imgUrl = URLDecoder.decode(fileName, "UTF-8");
            if (imgUrl.equals("false")) {
                return ResponseDto.fail("이미지 파일만 업로드 가능합니다.");
            }
            // 이미지 저장 및 이미지테이블이랑 포스트테이블매핑하기

            MultiImage multiImage = new MultiImage(imgUrl, auction.getId(), member.getId());

            multiImages.add(multiImage);

            imgUrlList.add(imgUrl);

            multiImgRepository.save(multiImage);

        }


        return ResponseDto.success(
                AuctionResponseDto.builder()
                        .auctionId(auction.getId())
                        .productName(auction.getProductName())
                        .memberId(member.getId())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
                        .title(auction.getTitle())
                        .content(auction.getContent())
                        .multiImages(auction.getMultiImages())
                        .startPrice(auction.getStartPrice())
                        .nowPrice(auction.getNowPrice())
                        .auctionPeriod(auction.getAuctionPeriod())
                        .category(auction.getCategory())
                        .region(auction.getRegion())
                        .direct(auction.isDirect())
                        .delivery(auction.isDelivery())
                        .viewerCnt(auction.getViewerCnt())
                        .auctionStatus(true)
                        .participantStatus(auction.isParticipantStatus())
                        .favoriteStatus(auction.isFavoriteStatus())
                        .createdAt(auction.getCreatedAt())
                        .modifiedAt(auction.getModifiedAt())
                        .build()
        );

    }

    @Transactional
    public ResponseDto<?> deleteAuction(Long auctionId,
                                        HttpServletRequest request) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        //수정시 해당 경매게시글에 있는 이미지 전체 삭제
        //multiImgRepository.deleteAllByMemberIdAndAuctionId(member.getId(), auctionId);

        auctionRepository.deleteById(auctionId);

        return ResponseDto.successToMessage(200,"게시물이 성공적으로 삭제되었습니다",null);


    }

    @Transactional
    public ResponseDto<?> findCategoryAuction(String category) {

        return ResponseDto.success(auctionRepository.findAllByCategory(category));

    }

    @Transactional
    public ResponseDto<?> findRegionAuction(String region) {

        return ResponseDto.success(auctionRepository.findAllByRegion(region));

    }


    public ResponseDto<?> findCategoryAndRegionAuction(String category, String region) {

        return ResponseDto.success(auctionRepository.findAllByCategoryAndRegion(category,region));

    }





//======================== 회원 정보 및 경매 정보 ========================

    @Transactional
    public Member validateMember(HttpServletRequest request) {

        return tokenProvider.getMemberFromAuthentication();
    }


    @Transactional(readOnly = true)
    public Auction checkAuction(Long id) {
        Optional<Auction> optionalAuction = auctionRepository.findById(id);
        return optionalAuction.orElse(null);
    }



    // ========================= 파일업로드 관련 메서드 ==========================

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {

        String type = file.getContentType();
        long size = file.getSize();
        System.out.println("====================================" + type);
        System.out.println("====================================" + size);

        // 파일 타입 예외처리
        if (!type.startsWith("image")) {

            throw new FileTypeErrorException();

        }

        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());


        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }


    // 로컬에 저장된 파일을 S3로 파일업로드 세팅 및 S3로 업로드
    public String upload(File uploadFile) {
        // S3에 저장될 파일 이름
        String fileName = "DdangDdang/auctionImg/" + UUID.randomUUID() + uploadFile.getName();
        // s3로 업로드 및  업로드 파일의 url을 String으로 받음.
        String uploadImageUrl = putS3(uploadFile, fileName);
        log.info(uploadImageUrl);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // 이미지 변환 예외처리
    public String upload(MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: 파일 변환에 실패했습니다"));

        return upload(uploadFile);
    }

    // S3로 업로드 하는 메서드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // s3에 파일 업로드 성공시 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }



}
