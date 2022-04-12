import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {ObjectInstance} from "../../../../api/storage-api";

@Component({
    selector: 'app-object-list-item',
    templateUrl: 'object-list-item.component.html',
    styleUrls: ['./object-list-item.component.css']
})
export class ObjectListItemComponent {
    @Input() objectInstance!: ObjectInstance
    @Input() observed!: boolean
    @Output() observationChange = new EventEmitter<string>();

    toggleSelected() {
        this.observed = !this.observed
        this.observationChange.emit(this.objectInstance.id)
    }
}
