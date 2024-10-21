# Data model
## Loan
   
- id: `string` - unique identifier
- debtor: `LoanDebtor`
- amount: `number`
- investors: `List<Investors>` - the list of users that lent the money for this loan