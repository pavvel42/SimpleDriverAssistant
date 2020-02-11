import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'

export const onUpdateCollectionUser = functions.firestore.document('users/{uid}/locationUser/{email}')
    .onUpdate((snap,context)=>{
        const objectUser = snap.after.data() 
        if(objectUser !== undefined){
            admin.firestore().collection('/report').get().then((zmienna)=>{
                zmienna.docs.forEach((report)=>{
                    const repid = report.id
                    const objectReport = report.data()
                    if(deleteOldReport(objectReport.time)>300000){
                        console.log("Usuwanie raportu: "+repid+" usera "+objectReport.email)
                        report.ref.delete().catch(problem=>{
                            console.log("Problem z usuwaniem raportu: "+problem)
                        })
                    } else if(objectUser.email !== objectReport.email){
                        console.log("Sprawdzam raport... usera "+objectReport.email+" dla usera "+objectUser.email)
                        const dist = distance(objectUser.longitude,objectUser.latitude,objectReport.longitude,objectReport.latitude)
                        if( dist < 999  ){
                            console.log("Tworzenie nowego raportu dla usera jako subcollection")
                            //const report4user = createReport4User(objectReport,dist)
                            const objectR4U = {
                                broadcaster : objectReport.email,
                                latitudeReport : objectReport.latitude,
                                longitudeReport : objectReport.longitude,
                                action : objectReport.action,
                                rating : objectReport.rating,
                                distance : dist,
                                reportid : repid,
                            }
                            console.log("Tworze nowy obiekt "+objectR4U)
                            admin.firestore().doc('users/'+objectUser.email+'/report4user/currentReport').set(objectR4U).catch(problem=>{
                                console.log("Problem z tworzeniem raportu dla usera "+problem)
                            })
                        }
                    }
                })
            }).catch(problem=>{
                console.log("Problem z Iteracją collection('/report') "+problem)
            })
        } else {
            console.log("Problem z obiektem User "+ objectUser)
        }
        return context;
    })

function distance(x1 : number,y1 : number, x2 : number, y2 : number) {
    let dis;
    dis = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((Math.cos((x1 * Math.PI) / 180) * (y2 - y1)), 2)) * (40075.704 / 360);
    console.log("x1 "+x1+" y1 "+y1+" x2 "+x2+" y2 "+y2+" różnica w odległości wynosi = "+dis*1000+" m")
    return dis*1000;
}


function deleteOldReport(timeRep:number){
    const time = Date.now();
    const diff = time - timeRep
    console.log("Czas serwera: "+time+" Czas raportu: "+timeRep+ " Różnica: "+diff)
    return diff
}    

// function createReport4User(obReport:object|any,diff:number){
//     if(obReport !== undefined){
//         const objectR4U = {
//             broadcaster : obReport.email,
//             latitudeReport : obReport.latitude,
//             longitudeReport : obReport.longitude,
//             action : obReport.action,
//             raiting : obReport.raiting,
//             distance : diff,
//         }
//         console.log("Tworze nowy obiekt "+objectR4U)
//         return objectR4U
//     }
// }