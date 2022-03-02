import {Component, EventEmitter, Input, Output} from "@angular/core";
import {ObjectInstance} from "../../../api/storage-api";

@Component({
    selector: 'app-object-list',
    templateUrl: 'object-list.component.html',
    styleUrls: ['./object-list.component.css']
})
export class ObjectListComponent {
    @Input() objectInstances!: ObjectInstance[]
    @Input() observedObjectIds!: Set<string>
    @Output() observedObjectIdsChange = new EventEmitter<Set<string>>();

    updateObservedObjectIds(objectId: string): void {
        if (this.observedObjectIds.has(objectId)) {
            this.observedObjectIds.delete(objectId)
        } else {
            this.observedObjectIds.add(objectId)
        }
        this.observedObjectIdsChange.emit(this.observedObjectIds)
    }
}
