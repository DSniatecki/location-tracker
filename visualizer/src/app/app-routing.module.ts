import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ObjectLocationMapComponent} from "./components/object-location/map/object-location-map.component";
import {ObjectLocationComponent} from "./components/object-location/object-location.component";

const routes: Routes = [
    {path: '', component: ObjectLocationComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
