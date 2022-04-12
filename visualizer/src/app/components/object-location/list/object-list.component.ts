import {Component, EventEmitter, Input, Output} from "@angular/core";
import {ObjectInstance} from "../../../api/storage-api";

@Component({
    selector: 'app-object-list',
    templateUrl: 'object-list.component.html',
    styleUrls: ['./object-list.component.css']
})
export class ObjectListComponent {
    @Input() objectInstances!: ObjectInstance[]
    @Input() observedObjectId?: string
    @Output() observedObjectIdChange = new EventEmitter<string>();

    updateObservedObjectIds(objectId: string): void {
        this.observedObjectId = objectId
        this.observedObjectIdChange.emit(objectId)

    }
}
