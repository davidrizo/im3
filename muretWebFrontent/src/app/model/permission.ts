import {Project} from './project';

export class Permission {
  id: number;
  permission: string;
  projects: Array<Project>;
}
