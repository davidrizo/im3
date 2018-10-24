import {Image} from './image';

export class Project {
  id: number;
  name: string;
  posterFramePath: string;
  images: Array<Image>;
  /*path: string;
  created: string;
  lastChange: string;*/

  constructor(id: number, name: string, posterFramePath: string, images: Array<Image>) {
    this.id = id;
    this.name = name;
    this.posterFramePath = posterFramePath;
    this.images = images;
  }
}
