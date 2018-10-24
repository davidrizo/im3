import {Page} from './page';

export class Image {
  id: number;
  path: string;
  pages: Array<Page>;

  constructor(id: number, path: string, pages: Array<Page>) {
    this.id = id;
    this.path = path;
    this.pages = pages;
  }
}
