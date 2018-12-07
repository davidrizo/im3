import {Project} from './project';
import {Permission} from './permission';

export class User {
  id: number;
  username: string;
  projectsCreated: Array<Project>;
  permissions: Array<Permission>;
}
