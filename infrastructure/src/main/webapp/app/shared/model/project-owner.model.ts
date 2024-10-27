export interface IProjectOwner {
  id?: number;
  name?: string;
  imageUrl?: string | null;
}

export const defaultValue: Readonly<IProjectOwner> = {};
