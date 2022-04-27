import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ObjectLocationMapComponent} from "./components/object-location/map/object-location-map.component";
import {ObjectLocationComponent} from "./components/object-location/object-location.component";
import {AuthGuard} from "./services/auth.guard";

const routes: Routes = [
    {path: '', component: ObjectLocationComponent, canActivate: [AuthGuard]},
    {path: '**', redirectTo: ''}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
