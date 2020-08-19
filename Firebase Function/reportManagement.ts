import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'

export const onReportCreate = functions.firestore.document('report/{time}')
    .onCreate((snap,context)=>{
        admin.firestore().collection('/report').get().then((snapshot)=>{
            snapshot.docs.forEach((doc)=>{
                console.log(doc.data().email)
                const timeReport = doc.data().time
                if(deleteOldReport(timeReport)>300000){
                    console.log("Usuwanie raportu "+doc.data())
                    doc.ref.delete().catch(problem=>{
                        console.log("report.ref.delete() "+problem)
                    })
                }
            })
        }).catch((error=>{
            console.log("error"+error)
        }));
        return false;
    })


function deleteOldReport(timeRep:number){
    const time = Date.now();
    const diff = time - timeRep
    console.log("Czas serwera: "+time+" Czas raportu: "+diff+ " Różnica: "+diff)
    return diff
}    