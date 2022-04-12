import {Component, Input} from "@angular/core";
import {ObjectLocation} from "../../../../api/archiver-api";

@Component({
    selector: 'app-object-location-map-popup',
    templateUrl: 'object-location-map-popup.component.html',
    styleUrls: ['./object-location-map-popup.component.css']
})
export class ObjectLocationMapPopupComponent {
    @Input() objectLocation!: ObjectLocation
}