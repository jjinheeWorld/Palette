package com.palette.domain.group;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupName;

    private String groupIntroduction;

    private  Long membersNumber;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberGroup> memberGroups = new ArrayList<>();

    @Builder
    public Group(String groupName,String groupIntroduction, Long membersNumber){
        this.groupName = groupName;
        this.groupIntroduction = groupIntroduction;
        this.membersNumber = membersNumber;
    }

    public void setMemberGroups(List<MemberGroup> memberGroups) {
        this.memberGroups = memberGroups;
    }
}
