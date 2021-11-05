package com.palette.service;

import com.palette.domain.Period;
import com.palette.domain.member.Member;
import com.palette.domain.post.MyFile;
import com.palette.domain.post.Post;
import com.palette.domain.post.PostGroup;
import com.palette.dto.SearchCondition;
import com.palette.dto.response.PostResponseDto;
import com.palette.dto.response.StoryListResponseDto;
import com.palette.repository.MemberRepository;
import com.palette.repository.PostGroupRepository;
import com.palette.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostServiceTest {

    @Autowired PostService postService;
    @Autowired LikeService likeService;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired PostGroupRepository postGroupRepository;

    @Test
    void 좋아요_개수_조회(){
        Member member = new Member("1234", "wogns", "wogns","123");
        memberRepository.save(member);

        PostGroup group = PostGroup.builder().member(member).title("하이").region("서울").period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                , LocalDateTime.of(2021, 11, 5, 20, 20))).build();
        postGroupRepository.save(group);

        Post post = Post.builder().title("제목입니다")
                .member(member)
                .content("내용")
                .region("서울")
                .period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                        , LocalDateTime.of(2021, 11, 5, 20, 20)))
                .build();
        postService.write(post, group.getId());

        likeService.pushLike(member,post.getId());

        List<StoryListResponseDto> storyList = postService.findStoryList(new SearchCondition(), 1);
        assertThat(storyList.get(0).getLikesCount()).isEqualTo(1);
    }

    @Test
    void 좋아요_더블클릭_취소(){
        Member member = new Member("1234", "wogns", "wogns","123");
        memberRepository.save(member);

        PostGroup group = PostGroup.builder().member(member).title("하이").region("서울").period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                , LocalDateTime.of(2021, 11, 5, 20, 20))).build();
        postGroupRepository.save(group);

        Post post = Post.builder().title("제목입니다")
                .member(member)
                .content("내용")
                .region("서울")
                .period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                        , LocalDateTime.of(2021, 11, 5, 20, 20)))
                .build();
        postService.write(post, group.getId());
        likeService.pushLike(member,post.getId());
        likeService.pushLike(member,post.getId());
        List<StoryListResponseDto> storyList = postService.findStoryList(new SearchCondition(), 1);
        assertThat(storyList.get(0).getLikesCount()).isEqualTo(0);

    }

    @Test
    void 기본_썸네일_조회(){
        Member member = new Member("1234", "wogns", "wogns", "123");
        memberRepository.save(member);

        PostGroup group = PostGroup.builder().member(member).title("하이").region("서울").period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                , LocalDateTime.of(2021, 11, 5, 20, 20))).build();
        postGroupRepository.save(group);

        Post post = Post.builder().title("제목입니다")
                .member(member)
                .content("내용")
                .region("서울")
                .period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                        , LocalDateTime.of(2021, 11, 5, 20, 20)))
                .build();
        postService.write(post, group.getId());

        List<StoryListResponseDto> storyList = postService.findStoryList(new SearchCondition(), 1);
        assertThat(storyList.get(0).getThumbNailFullPath()).isEqualTo("기본썸네일");
    }

    @Test
    void 지정_썸네일_조회(){
        Member member = new Member("1234", "wogns", "wogns","123");
        memberRepository.save(member);

        PostGroup group = PostGroup.builder().member(member).title("하이").region("서울").period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                , LocalDateTime.of(2021, 11, 5, 20, 20))).build();
        postGroupRepository.save(group);

        Post post = Post.builder().title("제목입니다")
                .member(member)
                .content("내용")
                .region("서울")
                .period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                        , LocalDateTime.of(2021, 11, 5, 20, 20)))
                .build();

        List<MyFile> myFiles = new ArrayList<>();
        myFiles.add(new MyFile("ab.jpg","ab.jpg"));
        postService.write(post,group.getId(),myFiles);


        Post post2 = Post.builder().title("제목입니다")
                .member(member)
                .content("내용")
                .region("서울")
                .period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                        , LocalDateTime.of(2021, 11, 5, 20, 20)))
                .build();
        List<MyFile> myFiles2 = new ArrayList<>();
        myFiles2.add(new MyFile("abc.jpg","abc.jpg"));
        myFiles2.add(new MyFile("abcd.jpg","abcd.jpg"));
        postService.write(post2,group.getId(),myFiles2);


        List<StoryListResponseDto> storyList = postService.findStoryList(new SearchCondition(), 1);
        assertThat(storyList.get(1).getThumbNailFullPath()).isEqualTo("ab.jpg");
        assertThat(storyList.get(0).getThumbNailFullPath()).isEqualTo("abc.jpg");
    }

    @Test
    void 단건_게시판_조회(){
        Member member = new Member("1234", "wogns", "wogns","123");
        memberRepository.save(member);

        PostGroup group = PostGroup.builder().member(member).title("하이").region("서울").period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                , LocalDateTime.of(2021, 11, 5, 20, 20))).build();
        postGroupRepository.save(group);

        Post post = Post.builder().title("제목입니다")
                .member(member)
                .content("내용")
                .region("서울")
                .period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                        , LocalDateTime.of(2021, 11, 5, 20, 20)))
                .build();
        List<MyFile> myFiles = new ArrayList<>();
        myFiles.add(new MyFile("abc.jpg","abc.jpg"));
        myFiles.add(new MyFile("abcd.jpg","abcd.jpg"));
        postService.write(post,group.getId(),myFiles);

        PostResponseDto singlePost = postService.findSinglePost(post.getId(), 0L);
        assertThat(singlePost.getMemberName()).isEqualTo("wogns");
        assertThat(singlePost.getPostTitle()).isEqualTo("제목입니다");
        assertThat(singlePost.getImages().size()).isEqualTo(2);
    }

    @Test
    void 복수_쿼리_확인(){
        Member member = new Member("1234", "wogns", "wogns", "123");
        memberRepository.save(member);
        Member findMember = memberRepository.findAll().get(0);

        PostGroup group = PostGroup.builder().member(member).title("하이").region("서울").period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                , LocalDateTime.of(2021, 11, 5, 20, 20))).build();
        postGroupRepository.save(group);

        for(int i = 0 ; i < 10 ; i++){
            String region = "서울";
            Post post = Post.builder().title("제목입니다" + i)
                    .member(findMember)
                    .content("내용")
                    .region(region)
                    .period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                            , LocalDateTime.of(2021, 11, 5, 20, 20)))
                    .build();

            postService.write(post,group.getId());
        }

        List<StoryListResponseDto> storyList = postService.findStoryList(new SearchCondition(), 1);
        assertThat(storyList.size()).isEqualTo(9);
    }

    @Test
    void 그룹내_포스트_조회(){
        Member member = new Member("1234", "wogns", "wogns", "123");
        memberRepository.save(member);
        PostGroup group = PostGroup.builder().member(member).title("하이").region("서울").period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                , LocalDateTime.of(2021, 11, 5, 20, 20))).build();

        PostGroup group2 = PostGroup.builder().member(member).title("하이").region("서울").period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                , LocalDateTime.of(2021, 11, 5, 20, 20))).build();

        postGroupRepository.save(group);
        postGroupRepository.save(group2);

        for(int i = 0 ; i < 10 ; i++){
            String region = "서울";
            Post post = Post.builder().title("제목입니다" + i)
                    .member(member)
                    .content("내용")
                    .region(region)
                    .period(new Period(LocalDateTime.of(2021, 11, 2, 20, 20)
                            , LocalDateTime.of(2021, 11, 5, 20, 20)))
                    .build();

            if(i > 7){
                postService.write(post, group2.getId());
            }else{
                postService.write(post, group.getId());
            }
        }
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setPostGroupId(group.getId());
        List<StoryListResponseDto> storyList = postService.findStoryList(searchCondition, 1);

        assertThat(storyList.size()).isEqualTo(8);
    }

    @AfterEach
    void tearDown(){
        System.out.println("================After Each====================");
        postRepository.deleteAll();
        postGroupRepository.deleteAll();
        memberRepository.deleteAll();
    }


}
