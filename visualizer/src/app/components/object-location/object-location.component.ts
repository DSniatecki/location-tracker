import {Component} from "@angular/core";
import {ObjectInstance} from "../../api/storage-api";
import {ObjectLocation} from "../../api/archiver-api";
import {ObjectService} from "../../services/object.service";
import {ObjectLocationService} from "../../services/object-location.service";

const TOLERANCE = 60 * 60 * 24 * 365
const REFRESH_RATE_MILLIS = 2000

@Component({
    selector: 'app-object-location',
    templateUrl: './object-location.component.html',
    styleUrls: ['./object-location.component.css']
})
export class ObjectLocationComponent {

    objectInstances: ObjectInstance[] = []
    observedObjectIds: Set<string> = new Set<string>()
    objectLocations: ObjectLocation[] = []

    constructor(
        protected readonly objectService: ObjectService,
        protected readonly objectLocationService: ObjectLocationService
    ) {
    }

    ngOnInit(): void {
        this.objectService.getAll()
            .subscribe(objectInstances => this.objectInstances = objectInstances)
        this.loadLocations()
    }

    changeObservedObjectIds(newObservedObjectIds: Set<string>): void {
        this.observedObjectIds = newObservedObjectIds
        this.objectLocations = this.objectLocations.filter(location => newObservedObjectIds.has(location.objectId))
    }

    private loadLocations(): void {
        if (this.observedObjectIds.size > 0) {
            this.objectLocationService.getMultipleEffective(this.observedObjectIds, TOLERANCE)
                .subscribe(locations => {
                    console.log(locations)
                    this.objectLocations = locations
                    setTimeout(() => this.loadLocations(), REFRESH_RATE_MILLIS)
                })
        } else {
            setTimeout(() => this.loadLocations(), REFRESH_RATE_MILLIS)
        }
    }
}