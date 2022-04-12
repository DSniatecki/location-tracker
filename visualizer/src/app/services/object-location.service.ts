import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {ObjectLocation, ObjectLocationControllerService} from "../api/archiver-api";


@Injectable()
export class ObjectLocationService {

    constructor(protected readonly objectLocationControllerService: ObjectLocationControllerService) {
    }

    getEffective(objectId: string, tolerance?: number): Observable<ObjectLocation> {
        return this.objectLocationControllerService.getEffectiveObjectLocation(objectId, undefined, tolerance)
    }

    getMultipleEffective(objectIds: Set<string>, tolerance?: number): Observable<ObjectLocation[]> {
        return this.objectLocationControllerService.getEffectiveObjectLocations(objectIds, undefined, tolerance)
    }
}