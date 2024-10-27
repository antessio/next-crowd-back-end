import dayjs from 'dayjs';
import { IProjectOwner } from 'app/shared/model/project-owner.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface ICrowdfundingProject {
  id?: number;
  title?: string;
  status?: keyof typeof Status;
  requestedAmount?: number;
  collectedAmount?: number | null;
  currency?: string;
  imageUrl?: string | null;
  projectStartDate?: dayjs.Dayjs | null;
  projectEndDate?: dayjs.Dayjs | null;
  numberOfBackers?: number | null;
  description?: string | null;
  longDescription?: string | null;
  projectVideoUrl?: string | null;
  risk?: number | null;
  expectedProfit?: number | null;
  minimumInvestment?: number | null;
  owner?: IProjectOwner | null;
}

export const defaultValue: Readonly<ICrowdfundingProject> = {};
