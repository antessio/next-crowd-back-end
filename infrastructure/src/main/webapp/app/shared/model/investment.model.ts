import { ICrowdfundingProject } from 'app/shared/model/crowdfunding-project.model';

export interface IInvestment {
  id?: number;
  bakerId?: string;
  amount?: number;
  moneyTransferId?: string | null;
  crowdfundingProject?: ICrowdfundingProject | null;
}

export const defaultValue: Readonly<IInvestment> = {};
