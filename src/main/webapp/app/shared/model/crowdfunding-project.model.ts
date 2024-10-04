import dayjs from 'dayjs';
import { ICrowdfundingProjectOwner } from 'app/shared/model/crowdfunding-project-owner.model';

export interface ICrowdfundingProject {
  id?: number;
  title?: string;
  requestedAmount?: number;
  collectedAmount?: number;
  currency?: string;
  imageUrl?: string | null;
  risk?: number;
  projectStartDate?: dayjs.Dayjs;
  projectEndDate?: dayjs.Dayjs;
  numberOfBackers?: number;
  summary?: string | null;
  description?: string | null;
  expectedProfit?: number;
  minimumInvestment?: number;
  projectVideoUrl?: string | null;
  owner?: ICrowdfundingProjectOwner | null;
}

export const defaultValue: Readonly<ICrowdfundingProject> = {};
