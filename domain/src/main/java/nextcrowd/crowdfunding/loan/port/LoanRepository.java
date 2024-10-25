package nextcrowd.crowdfunding.loan.port;

import java.util.Optional;

import nextcrowd.crowdfunding.loan.model.Loan;
import nextcrowd.crowdfunding.loan.model.LoanId;

public interface LoanRepository {

    void save(Loan loan);

    Optional<Loan> findById(LoanId loanId);

}
