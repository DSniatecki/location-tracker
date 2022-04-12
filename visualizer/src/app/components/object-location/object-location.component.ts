import {Component} from "@angular/core";
import {ObjectInstance} from "../../api/storage-api";
import {ObjectLocation} from "../../api/archiver-api";
import {ObjectService} from "../../services/object.service";
import {ObjectLocationService} from "../../services/object-location.service";

const TOLERANCE = 60 * 60 * 24 * 365

@Component({
    selector: 'app-object-location',
    templateUrl: './object-location.component.html',
    styleUrls: ['./object-location.component.css']
})
export class ObjectLocationComponent {

    objectInstances: ObjectInstance[] = []
    observedObjectId?: string
    observedObjectLocation?: ObjectLocation

    constructor(
        protected readonly objectService: ObjectService,
        protected readonly objectLocationService: ObjectLocationService
    ) {
    }

    ngOnInit(): void {
        this.loadObjects()
        this.loadLocation()
    }

    loadObjects(): void {
        this.objectService.getAll()
            .subscribe(objectInstances => this.objectInstances = objectInstances)
    }

    changeObservedObjectId(newObservedObjectId?: string): void {
        this.observedObjectId = newObservedObjectId
        this.loadLocation()
    }

    private loadLocation(): void {
        if (this.observedObjectId) {
            this.objectLocationService.getEffective(this.observedObjectId, TOLERANCE)
                .subscribe(location => this.observedObjectLocation = location)
        }
    }
}