package com.palette.service;

import com.palette.domain.group.Budget;
import com.palette.domain.group.Expense;
import com.palette.domain.group.Group;
import com.palette.domain.group.MemberGroup;
import com.palette.domain.member.Member;
import com.palette.dto.request.ExpenseDto;
import com.palette.dto.response.ExpenseResponseDto;
import com.palette.exception.BudgetException;
import com.palette.exception.GroupException;
import com.palette.repository.BudgetRepository;
import com.palette.repository.ExpenseRepository;
import com.palette.repository.GroupRepository;
import com.palette.repository.MemberGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ExpenseService {
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final BudgetService budgetService;
    private final MemberGroupRepository memberGroupRepository;

    //todo: 지출변경(단권삭제,전체삭제,단권수정)
    @Transactional
    public Expense addExpense(Member member,Group group,Expense expense,Budget budget){
        isGroupExist(group);
        isMemberHaveAuthToUpdate(member,group);
        isBudgetExist(group);
        //Expense savedExpense = expenseRepository.save(expense);
        expense.saveExpenseOnBudget(budget);
        return expense;
    }

    @Transactional
    public ExpenseResponseDto readExpenses(Member member,Long id){
        Group group = groupRepository.findById(id).orElse(null);
        isGroupExist(group);
        isMemberHaveAuthToUpdate(member,group);
        isBudgetExist(group);
        Budget findBudget = budgetRepository.findBudgetJoinWithGroup();

        long totalBudget = findBudget.getTotalBudget();
        long totalExpense = 0l;
        long remainingBudget = totalBudget;

        for(int i = 0; i < findBudget.getExpenses().size(); i++){
            totalExpense += findBudget.getExpenses().get(i).getPrice();
        }

        remainingBudget = totalBudget - totalExpense;

        //그룹에 해당되는 expense 들만 list에 담아서 보내주기
        List<Expense> findExpenses = expenseRepository.findByBudget(findBudget);

        List<ExpenseDto> expenseDtoList = makeExpenseDtoList(findExpenses);

        ExpenseResponseDto dto = new ExpenseResponseDto(id,totalBudget,totalExpense,remainingBudget,expenseDtoList);
        return dto;
    }

    //expense List-> dto List로 바꿔주기
    public List<ExpenseDto> makeExpenseDtoList(List<Expense> expenses){

        return expenses.stream().map(ExpenseDto::new).collect(Collectors.toList());

/*      테스트완료하면 지우기
        List<ExpenseDto> expenseDtos = new ArrayList<>();
        for(int i = 0; i < expenses.size(); i++){
            String category = expenses.get(i).getCategory().name();
            String detail = expenses.get(i).getDetail();
            long price = expenses.get(i).getPrice();
            expenseDtos.add(new ExpenseDto(category,detail,price));
        }
        return expenseDtos;
 */
    }

    //그룹이 존재하는지 확인
    private void isGroupExist(Group findGroup) {
        if (findGroup == null) {
            log.error("Group Not Exist Error");
            throw new GroupException("존재하지 않는 그룹입니다.");
        }
    }

    //그룹에 Budget이 존재하는지 확인
    private void isBudgetExist(Group group){
        Budget findBudget = budgetRepository.findBudgetJoinWithGroup();
        System.out.println("isBudget    " + findBudget);
        if(findBudget == null){
            log.error("Budget Not Exist Error");
            throw new BudgetException("예산이 존재하지 않습니다.");
        }
    }

    //지출 수정 권한이 있는지 확인 (그룹의 멤버인지 확인)
    public void isMemberHaveAuthToUpdate(Member member,Group group){
        MemberGroup memberGroup = memberGroupRepository.findByMemberAndGroup(member,group);
        if(memberGroup == null){
            log.error("Group Access Grant Error");
            throw new GroupException("그룹 접근 권한이 없습니다.");
        }
    }
}

