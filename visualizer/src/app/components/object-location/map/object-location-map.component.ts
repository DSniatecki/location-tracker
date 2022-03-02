import {AfterViewInit, Component, Input, OnChanges, SimpleChanges} from "@angular/core";
import {ObjectInstance} from "../../../api/storage-api";
import {ObjectLocation} from "../../../api/archiver-api";

@Component({
    selector: 'app-object-location-map',
    templateUrl: 'object-location-map.component.html',
    styleUrls: ['./object-location-map.component.css']
})
export class ObjectLocationMapComponent implements AfterViewInit, OnChanges {

    @Input() objectInstances: ObjectInstance[] = []
    @Input() objectLocations: ObjectLocation[] = []

    ngAfterViewInit(): void {
        // this.map = L.map('map', {center: [52.2812, 19.1795], zoom: 7, scrollWheelZoom: false});
        // L.tileLayer(OPEN_STREET_MAP_API_URL_TEMPLATE, {maxZoom: 18, minZoom: 3}).addTo(this.map);
    }

    ngOnChanges(changes: SimpleChanges): void {
    }

    getObjectInstance(objectId: string): ObjectInstance | undefined {
        return this.objectInstances.find(instance => instance.id == objectId)
    }
}
