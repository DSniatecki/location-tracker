import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {ObjectControllerService, ObjectInstance} from "../api/storage-api";


@Injectable()
export class ObjectService {

    constructor(protected readonly objectControllerService: ObjectControllerService) {
    }

    getAll(): Observable<ObjectInstance[]> {
        return this.objectControllerService.getObjects(undefined);
    }
}