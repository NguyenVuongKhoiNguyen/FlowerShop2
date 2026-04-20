package com.poly.models.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
	List<Reply> findByComment_Id(Long id);
}
