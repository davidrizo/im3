import {Image} from './image';

export class Project {
  id: number;
  name: string;
  path: string;
  thumbnailBase64Encoding: string;
  images: Array<Image>;
  comments: string;

  constructor(id: number, name: string, path: string, comments: string, thumbnailBase64Encoding: string, images: Array<Image>) {
    this.id = id;
    this.name = name;
    this.path = path;
    this.thumbnailBase64Encoding = thumbnailBase64Encoding;
    this.images = images;
    this.comments = comments;
  }
}
