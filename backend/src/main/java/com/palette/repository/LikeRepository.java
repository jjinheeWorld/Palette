package com.palette.repository;

import com.palette.domain.member.Member;
import com.palette.domain.post.Like;
import com.palette.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {
    Optional<Like> findByMemberAndPostId(Member member, Long postId);
}
