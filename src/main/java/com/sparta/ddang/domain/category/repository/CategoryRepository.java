package com.sparta.ddang.domain.category.repository;

import com.sparta.ddang.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByCategory(String category);
    void deleteByCategory(String category);
    Optional<Category> findByCategory(String cate);
    List<Category> findAllByOrderByViewerCntDesc();
    List<Category> findAllByOrderByCategoryAsc();

}
