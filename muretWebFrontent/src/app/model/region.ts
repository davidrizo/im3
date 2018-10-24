import {BoundingBox} from './bounding-box';

export class Region {
  id: number;
  boundingBox: BoundingBox;

  constructor(id: number, boundingBox: BoundingBox) {
    this.id = id;
    this.boundingBox = boundingBox;
  }
}
