import { Injectable } from '@angular/core';
import {Project} from './model/project';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {catchError, delay, map, share, tap} from 'rxjs/operators';
import {Image} from './model/image';
import {ConfigurationService} from './configuration.service';
import {ProjectURLS} from './model/project-urls';
import {NGXLogger} from 'ngx-logger';
import {StringReponse} from './string-reponse';
import {SVGSet} from './model/SVGSet';
import {Page} from './model/page';
import {ClassifierType} from './model/classifier-type';
import {Region} from './model/region';
import {Symbol} from './model/symbol';
import {User} from './model/user';
import {Strokes} from './model/strokes';
import {Point} from './model/point';
import {PostStrokes} from './payloads/post-strokes';
import {ProjectStatistics} from './model/project-statistics';
import {StringBody} from './payloads/string-body';
import {RegionType} from './model/region-type';
import {ITrainingSetExporter} from './model/itraining-set-exporter';

@Injectable({
  providedIn: 'root'
})

/**
 * This class has access to the IM3WS server, it includes the auth service
 */
export class Im3wsService {
  SESSION_USER_STORAGE = 'token';
  private urlProject: string;
  private urlImage: string;
  private urlSymbol: string;
  private urlLogin: string;
  private urlAuthenticatedUser: string;
  private urlAgnostic: string;
  private urlClassifierTypes: string;
  private urlRegionTypes: string;
  private urlUser: string;
  private urlTrainingSets: string;

  private user: User;
  // isLoggedIn: boolean;

  constructor(
    private configurationService: ConfigurationService,
    private http: HttpClient,
    private logger: NGXLogger) {

    this.logger.debug('Creating Im3wsService');
    this.urlLogin = configurationService.IM3WS_SERVER + '/muretapi/auth/login';  // URL to web api
    this.urlClassifierTypes = configurationService.IM3WS_SERVER + '/muretapi/classifiers';
    this.urlRegionTypes = configurationService.IM3WS_SERVER + '/muretapi/regiontypes';
    this.urlProject = configurationService.IM3WS_SERVER + '/muretapi/project';
    this.urlImage = configurationService.IM3WS_SERVER + '/muretapi/image';
    this.urlSymbol = configurationService.IM3WS_SERVER + '/muretapi/symbol';
    this.urlAgnostic = configurationService.IM3WS_SERVER + '/muretapi/agnostic';
    this.urlAuthenticatedUser = configurationService.IM3WS_SERVER + '/muretapi/auth/user';
    this.urlUser = configurationService.IM3WS_SERVER + '/muretapi/user';
    this.urlTrainingSets = configurationService.IM3WS_SERVER + '/muretapi/trainingsets';
  }

  logout(): void {
    // return this.im3wsService.logout();
   //  this.isLoggedIn = false;
    this.user = null;
    sessionStorage.removeItem(this.SESSION_USER_STORAGE);
  }

  authenticated(): boolean {
    // return this.isLoggedIn;
    return this.user != null;
  }

  getUser(): User {
    return this.user;
  }


  login(username: string, password: string): Observable<User> {
    return this.http.post<User>(this.urlLogin, {
      username: username,
      password: password
    }); /*.pipe(
      tap<User>(next => {
        if (next != null) {
          sessionStorage.setItem(
            this.SESSION_USER_STORAGE,
            btoa(username + ':' + password)
          );
          this.logger.error('Found user with id=' + next.id);
          Object.assign(this.user, next);
        }
      })
    );*/
  }

  getHttpAuthOptions() {
    const token = sessionStorage.getItem(this.SESSION_USER_STORAGE);
    if (!token) {
      throw new Error('User is not authorized yet');
    }

    const headers: HttpHeaders = new HttpHeaders({
      'Authorization': 'Bearer ' + token
    });
    const options = {headers: headers};
    return options;


    /*const headers_object = new HttpHeaders();
    headers_object.append('Content-Type', 'application/json');
    headers_object.append('Authorization', 'Basic ' + btoa('username:password'));
    headers_object.append('Access-Control-Allow-Methods', 'GET, POST, DELETE, PUT');

    const httpOptions = {
      headers: headers_object
    };
    return httpOptions;*/
  }

  checkAuthorized() {
    this.http.post<Observable<Object>>(this.urlAuthenticatedUser, {}, this.getHttpAuthOptions()).
    subscribe(principal => {
        console.log('Current user: ' + principal['name']);
      },
      error => {
        if (error.status === 401) {
          alert('Unauthorized');
        }
      }
    );
  }

    /*this.http.post<Observable<boolean>>(this.urlLogin, {
      username: username,
      password: password
    }).subscribe(isValid => {
      if (isValid) {
        sessionStorage.setItem(
          'token',
          btoa(username + ':' + password)
        );
        // router.navigate(['']);
        return true;
      } else {
        // alert('Authentication failed.');
        return false;
      }
    });*/

  getClassifierTypes$(): Observable<ClassifierType[]> {
    this.logger.debug('IM3WSService: fetching classifier types...');
    // TODO Pasarle usuario actual
    return this.http.get<ClassifierType[]>(this.urlClassifierTypes, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getClassifierTypes$', []))
      );

  }

  getRegionTypes(): Observable<RegionType[]> {
    this.logger.debug('IM3WSService: fetching region types...');
    return this.http.get<RegionType[]>(this.urlRegionTypes, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getRegionTypes', []))
      );

  }
  public getProjects$(): Observable<Project[]> {
    this.logger.debug('IM3WSService: fetching projects...');

    return this.http.get<Project[]>(this.urlProject, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getProjects$', []))
      );
  }

  public getUser$(id: number): Observable<User> {
    this.logger.debug('IM3WSService: fetching user ' + id + '...');

    return this.http.get<User>(this.urlProject + '/getlazy/' + id, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getUser$$', null))
      );
  }

  public newProject$(name: string, composer: string, notationType: string, manuscriptType: string, comments: string,
                     base64Thumbnail: string): Observable<Project> {
    this.logger.debug('IM3WSService: creating project with name ' + name);
    return this.http.post<Project>(this.urlProject + '/new', {
      'name': name,
      'composer': composer,
      'notationType': notationType,
      'manuscriptType': manuscriptType,
      'comments': comments,
      'thumbnailBase64Encoding': base64Thumbnail
    }, this.getHttpAuthOptions()).pipe(
        catchError(this.handleError('newProject$ with name=' + name, null))
      );
  }

  public getProject$(id: number): Observable<Project> {
    this.logger.debug('IM3WSService: fetching project with id ' + id);
    const result: Observable<Project> = this.http.get<Project>(this.urlProject + '/get/' + id, this.getHttpAuthOptions())
    .pipe(
        catchError(this.handleError('getProject$ with id=' + id, null))
      );
    this.logger.debug('IM3WSService: fetched ' + result.valueOf());
    return result;
  }

  public getProjectURLs$(id: number): Observable<ProjectURLS> {
    this.logger.debug('IM3WSService: fetching thumbnail URL of project with id ' + id);
    const result: Observable<ProjectURLS> = this.http.get<ProjectURLS>(this.urlProject + '/projectURLS/' + id, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getProject$ with id=' + id, null))
      );
    this.logger.debug('IM3WSService: fetched URL ' + result.valueOf());
    return result;
  }

  public getImage$(id: number): Observable<Image> {
    this.logger.debug('IM3WSService: fetching image with id ' + id);
    return this.http.get<Image>(this.urlImage + '/get/' + id, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getImage$ with id=' + id, null))
      );
  }


  splitPage(imageId: number, imageX: number): Observable<Array<Page>> {
    this.logger.debug('IM3WSService: splitting page image with id ' + imageId);
    return this.http.get<Array<Page>>(this.urlImage + '/pageSplit/' + imageId + '/' + imageX, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('splitPage with id=' + imageId, null))
      );
  }

  splitRegion(imageId: number, imageX: number, imageY: number): Observable<Array<Page>> {
    this.logger.debug('IM3WSService: splitting region image with id ' + imageId);
    return this.http.get<Array<Page>>(this.urlImage + '/regionSplit/' + imageId + '/' + imageX + '/' + imageY, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('splitRegion with id=' + imageId, null))
      );
  }

  clearDocumentAnalysis(imageId: number) {
    this.logger.debug('IM3WSService: clearing document analysis of image with id ' + imageId);
    return this.http.get<void>(this.urlImage + '/documentAnalysisClear/' + imageId, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('clearDocumentAnalysis with id=' + imageId, null))
      );
  }



  // TODO ¿Se está usando? - Si no, quitarlo
  public getSymbolsOfRegion$(regionID: number): Observable<Symbol> {
    this.logger.debug('IM3WSService: fetching symbols with region_id ' + regionID);
    return this.http.get<Symbol>(this.urlSymbol + '/region/' + regionID, this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('getSymbols$ with region_id=' + regionID, null))
      );
  }
  public getSVGFromAgnosticSymbolType$(notationType: string, manuscriptType: string, agnosticSymbolType: string)
    : Observable<StringReponse> {
    this.logger.debug('IM3WSService: fetching svg path for notationType=' + notationType
      + 'manuscriptType=' + manuscriptType
      + ' and agnosticSymbolType=' + agnosticSymbolType);
    return this.http.get<StringReponse>(this.urlAgnostic + '/svg'
      + '?notationType=' + notationType
      + '&manuscriptType=' + manuscriptType
      + '&symbolType=' + agnosticSymbolType
      ,
      this.getHttpAuthOptions(),
      )
      .pipe(
        catchError(this.handleError('getSVGFromAgnosticSymbolType ' + agnosticSymbolType, null))
      );
  }

  public setSVGSet$(notationType: string, manuscriptType: string): Observable<SVGSet> {
    this.logger.debug('IM3WSService: fetching svgset for notationType=' + notationType
      + 'manuscriptType=' + manuscriptType);
    return this.http.get<SVGSet>(this.urlAgnostic + '/svgset'
      + '?notationType=' + notationType
      + '&manuscriptType=' + manuscriptType
      ,
      this.getHttpAuthOptions(),
    )
      .pipe(
        catchError(this.handleError('getSVGScale', null))
      );
  }


  public saveProject(project: Project): Observable<any> {
    this.logger.debug('IM3WSService: saving project with id: ' + project.id);
    const result = this.http.put(this.urlProject, project, this.getHttpAuthOptions());
    result.subscribe(res => {
      console.log('Save project result: ' + res);
    });
    return result;
  }

  public saveProjectComposer(project: Project): Observable<any> {
    this.logger.debug('IM3WSService: saving project composer with id: ' + project.id);
    const stringBody = new StringBody(project.composer);
    const result = this.http.put(this.urlProject + '/composer/' + project.id, stringBody, this.getHttpAuthOptions());
    result.subscribe(res => {
      console.log('Save project composer result: ' + res);
    });
    return result;
  }

  public saveProjectComments(project: Project): Observable<any> {
    this.logger.debug('IM3WSService: saving project comments with id: ' + project.id);
    const stringBody = new StringBody(project.comments);
    const result = this.http.put(this.urlProject + '/comments/' + project.id, stringBody, this.getHttpAuthOptions());
    result.subscribe(res => {
      console.log('Save project comments result: ' + res);
    });
    return result;
  }

  public saveProjectState(project: Project): Observable<any> {
    this.logger.debug('IM3WSService: saving project state with id: ' + project.id);
    const result = this.http.put(this.urlProject + '/state/' + project.id, project.state, this.getHttpAuthOptions());
    result.subscribe(res => {
      console.log('Save project state result: ' + res);
    });
    return result;
  }

  public getProjectStatistics$(id: number)
    : Observable<ProjectStatistics> {
    this.logger.debug('IM3WSService: fetching project statistics for id ' + id);
      return this.http.get<StringReponse>(this.urlProject + '/statistics/' + id
      ,
      this.getHttpAuthOptions(),
    )
      .pipe(
        catchError(this.handleError('getProjectStatistics$ ' + id, null))
      );
  }

  updateRegionBoundingBox(id: number, fromX: number, fromY: number, toX: number, toY: number): Observable<any> {
    this.logger.debug('IM3WSService: updating region bounding box of id: ' + id);

    const result = this.http.get(this.urlImage + '/regionUpdate/' + id + '/'
      + fromX + '/' + fromY + '/'
      + toX  + '/' + toY
      , this.getHttpAuthOptions());
    result.subscribe(res => {
      console.log('Update region bounding box result: ' + res);
    });
    return result;

  }

  updateRegionType(id: number, regionType: RegionType): Observable<any> {
    this.logger.debug('IM3WSService: updating region type of id: ' + id + ' to ' + regionType.name);

    const result = this.http.get(this.urlImage + '/regionUpdateType/' + id + '/' + regionType.id
      , this.getHttpAuthOptions());
    result.subscribe(res => {
      console.log('Update region type update result: ' + res);
    });
    return result;

  }



  updatePageBoundingBox(id: number, fromX: number, fromY: number, toX: number, toY: number): Observable<any> {
    this.logger.debug('IM3WSService: updating page bounding box of id: ' + id);

    const result = this.http.get(this.urlImage + '/pageUpdate/' + id + '/'
      + fromX + '/' + fromY + '/'
      + toX  + '/' + toY
      , this.getHttpAuthOptions());
    result.subscribe(res => {
      console.log('Update page bounding box result: ' + res);
    });
    return result;
  }

  createSymbolFromBoundingBox(region: Region, fromX: number, fromY: number, toX: number, toY: number): Observable<Symbol> {
    this.logger.debug('IM3WSService: create symbol from bounding box in region ' + region.id);

    const result = this.http.get<Symbol>(this.urlImage + '/createSymbolFromBoundingBox/' + region.id + '/'
      + fromX + '/' + fromY + '/'
      + toX  + '/' + toY
      , this.getHttpAuthOptions())
      .pipe(share()); // if not, two calls are made for the same request due to CORS checking

    result.subscribe(res => {
      region.symbols.push(res); // the im3ws spring service just returns the new symbol, not the complete region on each symbol insert
      console.log('Symbol from bounding box in region ' + region.id);

    });
    return result;
  }

  createSymbolFromStrokes(region: Region, currentStrokes: Strokes): Observable<Symbol> {
    this.logger.debug('IM3WSService: create symbol from strokes in region ' + region.id);

    const points: Point[][] = [[]];
    currentStrokes.strokeList.forEach(stroke => {
      points.push(stroke.points);
    });

    const postStrokes = new PostStrokes(region.id, points);

    const result = this.http.post<Symbol>(this.urlImage + '/createSymbolFromStrokes', postStrokes
      , this.getHttpAuthOptions())
      .pipe(share()); // if not, two calls are made for the same request due to CORS checking

    result.subscribe(res => {
      region.symbols.push(res); // the im3ws spring service just returns the new symbol, not the complete region on each symbol insert
      console.log('Symbol from strokes in region ' + region.id);
    });
    return result;
  }


  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      this.logger.error(`${operation} failed: ${error.message}`);

      alert('Warning: ' + error.message);
      // Let the app keep running by returning an empty result.
      throw new Error(error.message);
    };
  }

  setUser(u: User) {
    this.user = Object.assign(new User(), u);

    sessionStorage.setItem(
      this.SESSION_USER_STORAGE,
      btoa(this.user.username)
    );
    console.log('ID: ' + this.user.id);
  }

  deleteSymbol(regionID: number, symbolID: number): Observable<any> {
    this.logger.debug('IM3WSService: deleting symbol from region with id ' + regionID + ' with symbol id: ' + symbolID);

    return this.http.get<boolean>(this.urlImage + '/removeSymbol/' + regionID + '/' + symbolID
      , this.getHttpAuthOptions());
  }
  changeAgnosticSymbolType(symbolID: number, agnosticSymbolTypeString: string): Observable<Symbol> {
    this.logger.debug('IM3WSService: changeAgnosticSymbolType with symbol id: ' + symbolID + ' to ' + agnosticSymbolTypeString);

    return this.http.get<Symbol>(this.urlSymbol + '/changeAgnosticSymbolType/' + symbolID + '/' + agnosticSymbolTypeString
      , this.getHttpAuthOptions());
  }

  changeAgnosticPositionInStaff(symbolID: number, agnosticSymbolPositionInStaff: string): Observable<Symbol> {
    this.logger.debug('IM3WSService: changeAgnosticSymbolType with symbol id: ' + symbolID + ' to ' + agnosticSymbolPositionInStaff);

    return this.http.get<Symbol>(this.urlSymbol + '/changeAgnosticPositionInStaff/' + symbolID + '/' + agnosticSymbolPositionInStaff
      , this.getHttpAuthOptions());
  }

  /**
   * @param upOrDown up | down
   */
  changeAgnosticPositionInStaffUpOrDown(symbolID: number, upOrDown: string): Observable<Symbol> {
    this.logger.debug('IM3WSService: changeAgnosticSymbolType with symbol id: ' + symbolID + ' ' + upOrDown);

    return this.http.get<Symbol>(this.urlSymbol + '/changeAgnosticPositionInStaffUpOrDown/' + symbolID + '/' + upOrDown
      , this.getHttpAuthOptions());
  }

  getTrainingSetExporters(): Observable<Array<ITrainingSetExporter>> {
    this.logger.debug('IM3WSService: fetching training set exporters');
    return this.http.get<Array<ITrainingSetExporter>>(this.urlTrainingSets + '/exporters', this.getHttpAuthOptions())
      .pipe(
        catchError(this.handleError('Fetch training set exporters', null))
      );
  }

  downloadTrainingSet(exporterIndex: number, projectIDS: Array<number>): Observable<any> {
    this.logger.debug('IM3WSService: fetching training set file for exporter ' + exporterIndex + ' and project ids: ' + projectIDS);
    let projectIdsString: string = null;
    projectIDS.forEach(id => {
      if (projectIdsString != null) {
        projectIdsString = projectIdsString + ',' + id;
      } else {
        projectIdsString = '' + id;
      }
    });
    const headers = new HttpHeaders();
    Object.assign(headers, this.getHttpAuthOptions());
    headers.append('Content-Type', 'application/x-gzip');

    /*return this.http.post<any>(this.urlTrainingSets + '/download/' + exporterIndex + '/' + projectIdsString,
      {headers, responseType: 'blob'})
      .pipe(
        catchError(this.handleError('Download training set', null))
      );*/

    return this.http.get(this.urlTrainingSets + '/download/' + exporterIndex + '/' + projectIdsString,
      { responseType: 'blob' }).pipe(
        catchError(this.handleError('Download training set', null))
      );
  }

}
