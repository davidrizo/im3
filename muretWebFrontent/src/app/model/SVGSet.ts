export class SVGSet {
  x: number;
  y: number;
  em: number;
  paths: Map<string, string>;


  constructor(x: number, y: number, em: number, paths: Map<string, string>) {
    this.x = x;
    this.y = y;
    this.em = em;
    this.paths = paths;
  }
}
