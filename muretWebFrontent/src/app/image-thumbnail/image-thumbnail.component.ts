import {Component, Input, OnInit} from '@angular/core';
import {Image} from '../model/image';
import {ProjectURLS} from '../model/project-urls';
import {NGXLogger} from 'ngx-logger';
import {Router} from '@angular/router';

@Component({
  selector: 'app-image-thumbnail',
  templateUrl: './image-thumbnail.component.html',
  styleUrls: ['./image-thumbnail.component.css']
})
export class ImageThumbnailComponent implements OnInit {
  @Input() projectURLs: ProjectURLS;
  @Input() image: Image;
  constructor(private logger: NGXLogger, private router: Router) {
  }

  ngOnInit() {
    // this.logger.debug('ImageThumbnails at: ' + this.projectURLs.thumbnails);
  }

  openImage(image: Image) {
    this.logger.debug('Opening image ' + image.id + ' in URL ' + this.projectURLs);
    this.router.navigate(['/image', image.id, this.projectURLs.masters]);
  }
}
