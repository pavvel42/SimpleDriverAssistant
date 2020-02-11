import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'
admin.initializeApp({
    credential: admin.credential.cert(require('../key/admin.json'))
});

//export * from './reportManagement';
export * from './onUpdateLocationUser';

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript
// firebase deploy --only functions:onUpdateLocationUser

//npm run-script lint
//npm run-script build
//firebase serve --only functions


export const helloWorld = functions.https.onRequest((request, response) => {
    const time = Date.now();
    

    const x1 = 54.366667; //long
    const y1 = 18.633333; //lati
    const x2 = 54.466667; //long
    const y2 = 17.016667; //lati

    function distance(xx1 : number,yy1 : number, xx2 : number, yy2 : number) {
        let dis;
        dis = Math.sqrt(Math.pow((xx2 - xx1), 2) + Math.pow((Math.cos((xx1 * Math.PI) / 180) * (yy2 - yy1)), 2)) * (40075.704 / 360);
        return dis*1000+"m";
    }

    const result = distance(x1, y1, x2, y2);
    console.log(result+" "+time);
    response.send("Hello from this Firecast! Czas:"+time+" Dystans: "+result); 
});


