import { TestBed, inject } from '@angular/core/testing';
import { HttpEvent, HttpEventType } from '@angular/common/http';

import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

import { ProjectService } from './project.service';

describe('ProjectService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService]
    });
  });

  it(
    'should get projects',
    inject(
      [HttpTestingController, ProjectService],
      (httpMock: HttpTestingController, projectService: ProjectService) => {
        const mockProjects = [
          { id: 1, name: 'Prueba Spring'}
        ];

        /*projectService.getProjects$().subscribe(value)
        projectService.getProjects$().subscribe((event: HttpEvent<any>) => {
          switch (event.type) {
            case HttpEventType.Response:
              expect(event.body).toEqual(mockProjects);
          }
        });*/

        const mockReq = httpMock.expectOne(projectService.urlProject);

        expect(mockReq.cancelled).toBeFalsy();
        expect(mockReq.request.responseType).toEqual('json');
        mockReq.flush(mockProjects);

        httpMock.verify();
      }
    )
  );
});

/* TO-DO Hacer este test */
