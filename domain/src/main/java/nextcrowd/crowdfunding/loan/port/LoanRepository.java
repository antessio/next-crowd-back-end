package nextcrowd.crowdfunding.loan.port;

import nextcrowd.crowdfunding.loan.model.Loan;

public interface LoanRepository {

    void save(Loan loan);

}
