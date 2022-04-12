import {Component, ComponentFactoryResolver, Injector, Input, OnChanges, OnInit, SimpleChanges} from "@angular/core";
import {ObjectLocation} from "../../../api/archiver-api";
import {MARKER_ICON, OPEN_STREET_MAP_API_URL_TEMPLATE} from "../../../config";
import * as L from "leaflet";
import {Content, LatLngExpression, Map, Marker} from "leaflet";
import {ObjectLocationMapPopupComponent} from "./popup/object-location-map-popup.component";

@Component({
    selector: 'app-object-location-map',
    templateUrl: 'object-location-map.component.html',
    styleUrls: ['./object-location-map.component.css']
})
export class ObjectLocationMapComponent implements OnInit, OnChanges {

    private map?: Map
    private marker?: Marker
    @Input() objectLocation?: ObjectLocation

    constructor(private readonly componentFactoryResolver: ComponentFactoryResolver,
                private readonly injector: Injector) {
    }

    ngOnInit(): void {
        this.initMap()
        this.updateMarker()
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.updateMarker()
    }

    private initMap() {
        this.map = L.map('map', {center: [52.2812, 19.1795], zoom: 0, scrollWheelZoom: false});
        L.tileLayer(OPEN_STREET_MAP_API_URL_TEMPLATE, {maxZoom: 18, minZoom: 3}).addTo(this.map);
    }

    private updateMarker() {
        if (this.marker) {
            this.marker.removeFrom(this.map!)
        }
        if (this.objectLocation) {
            const coordinates: LatLngExpression = [this.objectLocation.latitude, this.objectLocation.longitude]
            const marker = L.marker(coordinates, {icon: MARKER_ICON})
            marker
                .bindPopup(() => this.createPopup(this.objectLocation!))
                .addTo(this.map!);
            this.map!.flyTo(coordinates, 13)
            this.marker = marker
        }
    }

    private createPopup(objectLocation: ObjectLocation): Content {
        const factory = this.componentFactoryResolver.resolveComponentFactory(ObjectLocationMapPopupComponent);
        const component = factory.create(this.injector);
        component.instance.objectLocation = objectLocation;
        component.changeDetectorRef.detectChanges();
        return component.location.nativeElement
    }
}
