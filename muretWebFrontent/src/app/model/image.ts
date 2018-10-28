import {Page} from './page';

export class Image {
  id: number;
  filename: string;
  thumbnailFilename: string;
  pages: Array<Page>;

  constructor(id: number, filename: string, thumbnailFilename: string, pages: Array<Page>) {
    this.id = id;
    this.filename = filename;
    this.pages = pages;
    this.thumbnailFilename = thumbnailFilename;
  }
}
