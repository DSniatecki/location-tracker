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

@NgModule({
    declarations: [
        AppComponent,
        HeaderComponent,
        FooterComponent,
        ObjectLocationComponent,
        ObjectLocationMapComponent,
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
        {
            provide: STORAGE_API_BASE_PATH,
            useValue: "http://localhost:8000/storage/api"
        },
        {
            provide: ARCHIVER_API_BASE_PATH,
            useValue: "http://localhost:8000/archiver/api"
        }
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
