import { ICrowdfundingProject } from 'app/shared/model/crowdfunding-project.model';

export interface IProjectReward {
  id?: number;
  name?: string;
  imageUrl?: string | null;
  description?: string | null;
  crowdfundingProject?: ICrowdfundingProject | null;
}

export const defaultValue: Readonly<IProjectReward> = {};
