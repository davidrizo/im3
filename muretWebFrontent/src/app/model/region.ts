import {BoundingBox} from './bounding-box';
import {Symbol} from './symbol';

export class Region {
  id: number;
  boundingBox: BoundingBox;
  symbols: Array<Symbol>;

  constructor(id: number, boundingBox: BoundingBox, symbols: Array<Symbol>) {
    this.id = id;
    this.boundingBox = boundingBox;
    this.symbols = symbols;
  }
}
