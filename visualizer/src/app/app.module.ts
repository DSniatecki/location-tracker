import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FooterComponent} from "./components/footer/footer.component";
import {HeaderComponent} from "./components/header/header.component";
import {ObjectLocationMapComponent} from "./components/object-location/map/object-location-map.component";
import {HttpClientModule} from "@angular/common/http";
import {BASE_PATH as STORAGE_API_BASE_PATH} from "./api/storage-api";
import {BASE_PATH as ARCHIVER_API_BASE_PATH} from "./api/archiver-api";
import {ObjectService} from "./services/object.service";
import {ObjectLocationService} from "./services/object-location.service";
import {ObjectLocationComponent} from "./components/object-location/object-location.component";
import {ObjectListComponent} from "./components/object-location/list/object-list.component";
import {ObjectListItemComponent} from "./components/object-location/list/item/object-list-item.component";
import {
    ObjectLocationMapPopupComponent
} from "./components/object-location/map/popup/object-location-map-popup.component";
import {ARCHIVER_API_URL, STORAGE_API_URL} from "./config";

@NgModule({
    declarations: [
        AppComponent,
        HeaderComponent,
        FooterComponent,
        ObjectLocationComponent,
        ObjectLocationMapComponent,
        ObjectLocationMapPopupComponent,
        ObjectListComponent,
        ObjectListItemComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
    ],
    providers: [
        ObjectService,
        ObjectLocationService,
        {provide: STORAGE_API_BASE_PATH, useValue: STORAGE_API_URL},
        {provide: ARCHIVER_API_BASE_PATH, useValue: ARCHIVER_API_URL}
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
