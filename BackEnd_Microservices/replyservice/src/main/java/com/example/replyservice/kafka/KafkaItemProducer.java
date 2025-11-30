package com.example.replyservice.kafka;

import com.example.replyservice.InfluxdbRepository;
import com.example.replyservice.model.*;
import com.example.replyservice.tools.DataFetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import java.util.Random;

@Component
public class KafkaItemProducer {

    @Autowired
    private KafkaTemplate<String, JsonNotification> kafkaTemplate;
    @Autowired
    private InfluxdbRepository influxdbRepository;
    public void sendNotification(JsonNotification notification, String topic) {
        String key = generateKey(notification);

        sendToKafka(notification, key, topic);
    }
    private void sendToKafka(JsonNotification notification, String key, String topic) {
        CompletableFuture<SendResult<String, JsonNotification>> future = kafkaTemplate.send(topic, key, notification);
        future.thenAccept(result -> {
            System.out.printf("✅ Sent [%s] to topic [%s] at offset [%d]%n",
                    notification.getClass().getSimpleName(),
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().offset());
        }).exceptionally(ex -> {
            System.err.printf("❌ Failed to send [%s]: %s%n",
                    notification.getClass().getSimpleName(),
                    ex.getMessage());
            return null;
        });
    }

    private String generateKey(JsonNotification notification) {
        if (notification instanceof ItemNotification itemNotif) {
            return String.valueOf(itemNotif.getItemId());
        } else if (notification instanceof EventNotification eventNotif) {
            return String.valueOf(eventNotif.getItemId());
        } else if (notification instanceof RequestResponse response) {
            return String.valueOf(response.getRequestID());
        }
        return "unknown";
    }

    public void sendSampleNotifications(String topic, String dataset, String windowSize) {
        List<JsonNotification> notifications = createNotifications20(dataset);
            long timetste =1456699383963L;

        for (JsonNotification notification : notifications) {
          //  sendNotification(notification, topic);

            if (notification instanceof ItemNotification itemNotif) {
            sendNotification(notification, topic);
             //        System.out.printf("✅ Sent eventNotif "+notification.toString());

            } else if (notification instanceof EventNotification eventNotif) {
                Long randomId = eventNotif.getEventId();
       //       System.out.printf("✅ Sent eventNotif "+notification.toString());
                timetste=2956+timetste;
               RequestResponse response = new RequestResponse(randomId, eventNotif.getItemId(), eventNotif.getUserId(), timetste,  eventNotif.getGeoUser(),  eventNotif.getDeviceType());            
              
              
              
               List<Long> recommendations = new ArrayList<>();
                List<JsonNotification> similarEventNotifications = getNextSimilarEventNotifications(notifications, notification,5);
                for (JsonNotification simNotif : similarEventNotifications) {
              //  System.out.println("📌 Found similar EventNotification  : wft" + simNotif);
                    if (simNotif instanceof EventNotification eventNotif2  && eventNotif2.getUserId() == eventNotif.getUserId()) {
                            recommendations.add(eventNotif2.getItemId());
                    }
                }
        //     System.out.println("📌 Found similar EventNotification  : wft" + recommendations.toString());

                response.setRecs(recommendations);
                JsonNotification notification1 =response;
         //       System.out.printf("✅ Sent response "+notification1.toString());






           //    if (response.getRecs().isEmpty()) {
                 sendNotification(notification1, topic);
            //     System.out.println("✅ Sent response "+topic);

              //  }






              sendNotification(notification, topic);
            } else if (notification instanceof RequestResponse response) {
               // sendNotification(notification, topic);
            }
        }
    }








    

    

        public List<JsonNotification> createNotifications20(String dataset)  {
           DataFetcher dataFetcher = new DataFetcher(influxdbRepository);
/*
        String startDate = "26/08/2001 06:57:05";
        String endDate = "28/02/2016 01:19:53";
      List<List<JsonNotification>> chunks = dataFetcher.fetchInChunks(startDate, endDate, 6);

        String startDate1 = "26/08/2001 06:57:05";
        String endDate1 = "28/02/2016 06:19:53";
      List<List<JsonNotification>> chunks1 = dataFetcher.fetchInChunks(startDate1, endDate1, 6);
     
*/
     TimeRange timeRange= getTimeRangeByDataset( dataset);

        List<List<JsonNotification>> chunks1 = dataFetcher.fetchInChunks(timeRange.itemStart, timeRange.itemEnd, 6);
             List<List<JsonNotification>> chunks = dataFetcher.fetchInChunks(timeRange.eventStart, timeRange.eventEnd, 6);

        List<JsonNotification> chunks3 = new ArrayList<>();

                for (int i = 0; i < chunks1.size(); i++) {
        //    System.out.println("✅ Chunk " + (i + 1) + ":");
            for (JsonNotification notification : chunks1.get(i)) {
           //     System.out.println(notification);
                                chunks3.add(notification);

            }

        }
                for (int i = 0; i < chunks.size(); i++) {
          //  System.out.println("✅ Chunk " + (i + 1) + ":");
            for (JsonNotification notification : chunks.get(i)) {
                chunks3.add(notification);
         //       System.out.println(notification);
            }
        }
   //   chunks1.addAll(chunks);
        return chunks3; // Return the first chunk for demonstration purposes

    }



            public List<JsonNotification> getNextSimilarEventNotifications(
                    List<JsonNotification> notifications,
                    JsonNotification currentNotification,int wft
            ) {
                List<JsonNotification> similarEventNotifications = new ArrayList<>();

                if (!(currentNotification instanceof EventNotification currentEventNotif)) {
                    return similarEventNotifications;
                }

                long baseTimestamp = currentEventNotif.getTimestamp();
                long fiveMinutesLater = baseTimestamp + 5 * 60 * 1000;

                int currentIndex = notifications.indexOf(currentNotification);
                for (int i = currentIndex + 1; i < notifications.size(); i++) {
                    JsonNotification nextNotif = notifications.get(i);
                    if (nextNotif instanceof EventNotification nextEventNotif) {
                        if (nextEventNotif.getTimestamp() <= fiveMinutesLater) {
                            similarEventNotifications.add(nextNotif);
                        } else {
                            break; 
                        }
                    }
                }

                return similarEventNotifications;
            }





public TimeRange getTimeRangeByDataset(String dataset) {
    String startDate = "31/02/2016 04:19:53";
    switch (dataset) {
        case "6H":
            return new TimeRange(
                "01/02/2016 00:19:53", "01/02/2016 06:19:53", 1572,
                "26/09/2001  06:57:05", "01/02/2016 05:11:24", 536,
                6
            );
        case "1J":
            return new TimeRange(
                "01/02/2016 00:19:53", "02/02/2016 00:19:53", 21291,
                "26/09/2001 06:57:05", "01/02/2016 11:04:43", 563,
                6
            );
        case "1W":
            return new TimeRange(
                "01/02/2016 00:19:53", "07/02/2016 11:00:00", 476703,
                "26/09/2001 06:57:05", "07/02/2016 11:00:00", 654,
                6
            );
        case "1M":
            return new TimeRange(
                "01/02/2016 00:19:53", "28/02/2016 11:00:00", 2044722,
                "26/09/2001 06:57:05", "01/02/2016 00:19:53", 1089,
                6
            );
        default:
            return new TimeRange(
                "01/02/2016 00:19:53", "01/02/2016 06:19:53", 1572,
                "26/09/2001 06:57:05", "30/01/2016 05:11:24", 536,
                6
            );
        
    }
}
















/* */


    public List<JsonNotification> createNotifications() {
        List<JsonNotification> notifications = new ArrayList<>();
    
        notifications.add(new ItemNotification("Document", "418", "2001-08-26 06:57:05", 97592046L, true, "http://www.ksta.de/region/toleranz-training-nach-noten 15189102 14563704.html", "Toleranz-Training nach Noten", "2879993", "Die Dezibelzahl war ohrenbetäubend  die Botschaft eindeutig: Um Spaß  Musik und darum  miteinander etwas Großes auf die Beine zu stellen  ging es beim Lei-F-Style-Open-Air  das am...", "", "0#34#2879993"));
        notifications.add(new ItemNotification("Document", "418", "2001-08-27 06:34:01", 259299555L,true, "http://www.ksta.de/politik/die-volksfront-fuer-die-befreiung-palaestinas 15187246 14562948.html", "Die Volksfront für die Befreiung Palästinas", "0", "Die Volksfront für die Befreiung Palästinas (PFLP) wurde 1967 von gegründet. Sie ist marxistisch-leninistisch orientiert und lehnt den Friedensprozess mit Israel entschieden ab...", "", "0#20"));
        notifications.add(new ItemNotification("Document", "418", "2001-10-04 12:47:35", 133125713L, true, "http://www.ksta.de/sport/niklas-schneider-wusste-zu-ueberzeugen 15189364 14546012.html", "Niklas Schneider wusste zu überzeugen", "2881177", "Spannende Gefechte beim 13. Internationaen Tolbiac-Florett-Turnier und bei den Rheinischen Senioren-Einzellandesmeisterschaften in Zülpich.", "", "0#2881177#2"));
        notifications.add(new ItemNotification("Document", "418", "2001-10-25 04:10:58", 101548478L, true, "http://www.ksta.de/archiv/prozess-um-zugunglueck-von-bruehl-eingestellt 16592382 14535864.html", "Prozess um Zugunglück von Brühl eingestellt", "2879385", "Das spektakuläre Zugunglück von Brühl mit neun Toten und 149 Verletzten bleibt strafrechtlich ungesühnt. Das Kölner Landgericht stellte am Donnerstag nach knapp fünfmonatigem Prozess das Verfahren wegen geringer Schuld der vier Angeklagten ein.", "", "2879385#0#41"));
        notifications.add(new ItemNotification("Document", "418", "2001-11-03 06:27:24", 257971275L, true, "http://www.ksta.de/archiv/mehreinsatz-der-linie-19-geht-zu-lasten-anderer-bereiche 16592382 14531652.html?originalReferrer=https://www.google.de", "Mehreinsatz der Linie 19 geht zu Lasten anderer Bereiche", "0", "Als ich vor einigen Jahren aus meiner Heimatstadt Köln nach Hermülheim zog  war ich von der Idee einer direkten Straßenbahnverbindung in die Kölner City sehr angetan.", "", "0"));
        notifications.add(new ItemNotification("Document", "418", "2001-11-15 05:54:55", 89102479L, true, "http://www.ksta.de/koeln/--132wir-wollen-einen-kleinen-frank--148 15187530 14525010.html", "&#132Wir wollen einen kleinen Frank&#148", "2875514", "Frank Fußbroich wird seine Freundin Claudia Anfang des Jahres heiraten - auf dem Standesamt im Historischen Rathaus.", "", "0#2875514#31"));
        notifications.add(new ItemNotification("Document", "418", "2001-12-20 06:16:18", 263283273L, true, "http://www.ksta.de/region/reptilien-im-stich-gelassen 15189102 14506644.html", "Reptilien im Stich gelassen", "0", "Das Terrarium Tropical Fauna wurde vom Kreisveterinäramt und dem Ordnungsamt geräumt.", "", "0#34"));
        notifications.add(new ItemNotification("Document", "418", "2002-01-07 05:17:34", 259474094L, true, "http://www.ksta.de/region/-traumhafte-kennzahlen- 15189102 14500426.html", "Traumhafte Kennzahlen", "0", "In Sachen Standortqualität liegt die Stadt Bonn im bundesweiten Vergleich auf einem Spitzenplatz. Dies belegt eine neuerliche Untersuchung.", "", "0#34"));
        notifications.add(new ItemNotification("Document", "418", "2002-01-13 05:43:50", 128335590L, true, "http://www.ksta.de/kultur/--132ich-koennte-nicht-anders-leben--148 15189520 14497408.html?sa=X&ved=0ahUKEwiNkrOVmfXKAhXGKJoKHTmZDGkQ_h0IKzAH", "&#132Ich könnte nicht anders leben&#148", "2880191", "Theater gehört zu ihrem Leben. Aber auch die Familie. Ingrid Andree spricht über sich und die Welt.", "", "0#2880191"));
        notifications.add(new ItemNotification("Document", "418", "2002-03-27 06:36:13", 134189357L, true, "http://www.ksta.de/koeln/buehne-frei-fuer-koelschen-kluengel 15187530 14456648.html", "Bühne frei für kölschen Klüngel", "2875514", "Kölner Skandale waren schon immer die Süffigsten. Jetzt befasst sich ein Theaterstück mit der legendären Herstatt-Pleite: Sechs junge Bankmitarbeiter - die so genannten Goldjungs - hatten Anfang der Mitte der 70-er Jahre mit ungedeckten Milliardenbeträgen", "", "0#2875514#31"));
        notifications.add(new ItemNotification("Document", "418", "2002-05-22 12:27:39", 124624171L, true, "http://www.ksta.de/archiv/schoepferin-gewaltiger--nanas----niki-de-saint-phalle-gestorben 16592382 14428498.html", "Schöpferin gewaltiger \"Nanas\" - Niki de Saint Phalle gestorben", "2879385", "Unauffälliges hat die Künstlerin Niki de Saint Phalle  die jetzt nach schwerer Krankheit im Alter von 71 Jahren in San Diego in den USA gestorben ist  nie geschaffen. Ihre Monster- Frauen  die \"Nanas\"  sind überdimensionierte  ebenso bunte wie üppig verlo", "", "2879385#0#41"));
        notifications.add(new ItemNotification("Document", "418", "2002-06-24 07:02:10", 86561854L, true, "http://www.ksta.de/archiv/deutsche-sind-1953-fuer-westintegration 16592382 13828680.html", "Deutsche sind 1953 für Westintegration", "2879385", "Der Wahlkampf 1953 wird überschattet vom Aufstand in der DDR am 17. Juni. Die Wähler im Westen glauben nicht mehr an die Versprechen der SED und der UdSSR zur Wiedervereinigung. Mit der Angst vor der kommunistischen Bedrohung wächst die Zustimmung für Kon", "", "2879385#0#41"));
        notifications.add(new ItemNotification("Document", "418", "2002-07-12 06:51:53", 88741256L, true, "http://www.ksta.de/koeln/frank-fussbroich-wegen-hehlerei-verurteilt 15187530 14399856.html", "Frank Fussbroich wegen Hehlerei verurteilt", "2875514", "Ein paar Paletten gestohlenen Kaffees haben den prominenten TV-Sohn auf die Anklagebank gebracht. Polizisten hatten den Deal mit dem Diebesgut beobachtet.", "", "0#2875514#31"));
        notifications.add(new ItemNotification("Document", "418", "2002-10-20 05:58:26", 140076324L, false, "http://www.ksta.de/region/jubilaeumsrennen-war-gleichzeitig-das-letzte 15189102 14349432.html", "Jubiläumsrennen war gleichzeitig das letzte", "2879993", "Zur Tradition geworden war der Drei-Flüsse-Lauf. Doch der 25. Lauf entlang Dhünn  Wupper und Rhein war der letzte.", "", "0#34#2879993"));
        notifications.add(new ItemNotification("Document", "418", "2002-11-15 09:43:00", 126677549L, false, "http://www.ksta.de/wir-helfen/talismane-fuer--wir-helfen--versteigert 15189124 14335458.html", "Talismane für „Wir helfen“ versteigert", "2880273", "Das rege Interesse im studio dumont konnte nicht wirklich überraschen. Denn es ging am Freitagabend dort um eine Schmuck-Versteigerung der außergewöhnlichen Art: Prominente Persönlichkeiten hatten für \"wir helfen\" und unicef Erinnerungsstücke und Reisemit", "", "0#198#2880273"));
       
       
        notifications.add(new EventNotification("target", 140076324L, 0L, 1454281200322L, 18850L, 504183L));
        notifications.add(new EventNotification("target", 263283273L, 0L, 1454281199725L, 2878799L, 504183L));
        notifications.add(new EventNotification("target", 133125713L, 37160546266831020L, 1454281202545L, 18850L, 654013L));
        notifications.add(new EventNotification("target", 101548478L, 4560325L, 1454281204334L, 2878799L, 504183L));    
        return notifications;               
    }



 



    public static long generateRandomIdRequest() {
        long currentTimeMillis = System.currentTimeMillis();
        Random random = new Random();
        int randomNumber = random.nextInt(1001);
        return Long.parseLong(currentTimeMillis + "" + randomNumber);
    }




































    
    private List<JsonNotification> createNotifications2() {
        List<JsonNotification> notifications = new ArrayList<>();
       notifications.add(new ItemNotification("Document", "418", "2001-08-26 06:57:05", 97592046L, true, "http://www.ksta.de/region/toleranz-training-nach-noten 15189102 14563704.html", "Toleranz-Training nach Noten", "2879993", "Die Dezibelzahl war ohrenbetäubend  die Botschaft eindeutig: Um Spaß  Musik und darum  miteinander etwas Großes auf die Beine zu stellen  ging es beim Lei-F-Style-Open-Air  das am...", "", "0#34#2879993"));
       notifications.add(new ItemNotification("Document", "418", "2001-08-27 06:34:01", 259299555L,true, "http://www.ksta.de/politik/die-volksfront-fuer-die-befreiung-palaestinas 15187246 14562948.html", "Die Volksfront für die Befreiung Palästinas", "0", "Die Volksfront für die Befreiung Palästinas (PFLP) wurde 1967 von gegründet. Sie ist marxistisch-leninistisch orientiert und lehnt den Friedensprozess mit Israel entschieden ab...", "", "0#20"));
       notifications.add(new ItemNotification("Document", "418", "2001-10-04 12:47:35", 133125713L, true, "http://www.ksta.de/sport/niklas-schneider-wusste-zu-ueberzeugen 15189364 14546012.html", "Niklas Schneider wusste zu überzeugen", "2881177", "Spannende Gefechte beim 13. Internationaen Tolbiac-Florett-Turnier und bei den Rheinischen Senioren-Einzellandesmeisterschaften in Zülpich.", "", "0#2881177#2"));
       notifications.add(new ItemNotification("Document", "418", "2001-10-25 04:10:58", 101548478L, true, "http://www.ksta.de/archiv/prozess-um-zugunglueck-von-bruehl-eingestellt 16592382 14535864.html", "Prozess um Zugunglück von Brühl eingestellt", "2879385", "Das spektakuläre Zugunglück von Brühl mit neun Toten und 149 Verletzten bleibt strafrechtlich ungesühnt. Das Kölner Landgericht stellte am Donnerstag nach knapp fünfmonatigem Prozess das Verfahren wegen geringer Schuld der vier Angeklagten ein.", "", "2879385#0#41"));
     notifications.add(new ItemNotification("Document", "418", "2001-11-15 05:54:55", 89102479L, true, "http://www.ksta.de/koeln/--132wir-wollen-einen-kleinen-frank--148 15187530 14525010.html", "&#132Wir wollen einen kleinen Frank&#148", "2875514", "Frank Fußbroich wird seine Freundin Claudia Anfang des Jahres heiraten - auf dem Standesamt im Historischen Rathaus.", "", "0#2875514#31"));
  
 /*  //  notifications.add(new ItemNotification("Document", "418", "2002-07-12 06:51:53", 88741256L, true, "http://www.ksta.de/region/toleranz-training-nach-noten 15189102 14563704.html", "Toleranz-Training nach Noten", "2879993", "Die Dezibelzahl war ohrenbetäubend  die Botschaft eindeutig: Um Spaß  Musik und darum  miteinander etwas Großes auf die Beine zu stellen  ging es beim Lei-F-Style-Open-Air  das am...", "", "0#34#2879993"));
     notifications.add(new ItemNotification("Document", "418", "2001-08-27 06:34:01", 259299555L,true, "http://www.ksta.de/politik/die-volksfront-fuer-die-befreiung-palaestinas 15187246 14562948.html", "Die Volksfront für die Befreiung Palästinas", "0", "Die Volksfront für die Befreiung Palästinas (PFLP) wurde 1967 von gegründet. Sie ist marxistisch-leninistisch orientiert und lehnt den Friedensprozess mit Israel entschieden ab...", "", "0#20"));
     notifications.add(new ItemNotification("Document", "418", "2001-10-04 12:47:35", 133125713L, true, "http://www.ksta.de/sport/niklas-schneider-wusste-zu-ueberzeugen 15189364 14546012.html", "Niklas Schneider wusste zu überzeugen", "2881177", "Spannende Gefechte beim 13. Internationaen Tolbiac-Florett-Turnier und bei den Rheinischen Senioren-Einzellandesmeisterschaften in Zülpich.", "", "0#2881177#2"));
     notifications.add(new ItemNotification("Document", "418", "2001-10-25 04:10:58", 101548478L, true, "http://www.ksta.de/archiv/prozess-um-zugunglueck-von-bruehl-eingestellt 16592382 14535864.html", "Prozess um Zugunglück von Brühl eingestellt", "2879385", "Das spektakuläre Zugunglück von Brühl mit neun Toten und 149 Verletzten bleibt strafrechtlich ungesühnt. Das Kölner Landgericht stellte am Donnerstag nach knapp fünfmonatigem Prozess das Verfahren wegen geringer Schuld der vier Angeklagten ein.", "", "2879385#0#41"));
   notifications.add(new ItemNotification("Document", "418", "2001-11-15 05:54:55", 89102479L, true, "http://www.ksta.de/koeln/--132wir-wollen-einen-kleinen-frank--148 15187530 14525010.html", "&#132Wir wollen einen kleinen Frank&#148", "2875514", "Frank Fußbroich wird seine Freundin Claudia Anfang des Jahres heiraten - auf dem Standesamt im Historischen Rathaus.", "", "0#2875514#31"));

*/ 


     // notifications.add(new ItemNotification("Document", "418", "2002-07-12 06:51:53", 88741256L, true, "http://www.ksta.de/koeln/frank-fussbroich-wegen-hehlerei-verurteilt 15187530 14399856.html", "Frank Fussbroich wegen Hehlerei verurteilt", "2875514", "Ein paar Paletten gestohlenen Kaffees haben den prominenten TV-Sohn auf die Anklagebank gebracht. Polizisten hatten den Deal mit dem Diebesgut beobachtet.", "", "0#2875514#31"));
 
      /* 
       notifications.add(new ItemNotification("Document", "418", "2002-05-22 12:27:39", 124624171L, true, "http://www.ksta.de/archiv/schoepferin-gewaltiger--nanas----niki-de-saint-phalle-gestorben 16592382 14428498.html", "Schöpferin gewaltiger \"Nanas\" - Niki de Saint Phalle gestorben", "2879385", "Unauffälliges hat die Künstlerin Niki de Saint Phalle  die jetzt nach schwerer Krankheit im Alter von 71 Jahren in San Diego in den USA gestorben ist  nie geschaffen. Ihre Monster- Frauen  die \"Nanas\"  sind überdimensionierte  ebenso bunte wie üppig verlo", "", "2879385#0#41"));
       notifications.add(new ItemNotification("Document", "418", "2002-06-24 07:02:10", 86561854L, true, "http://www.ksta.de/archiv/deutsche-sind-1953-fuer-westintegration 16592382 13828680.html", "Deutsche sind 1953 für Westintegration", "2879385", "Der Wahlkampf 1953 wird überschattet vom Aufstand in der DDR am 17. Juni. Die Wähler im Westen glauben nicht mehr an die Versprechen der SED und der UdSSR zur Wiedervereinigung. Mit der Angst vor der kommunistischen Bedrohung wächst die Zustimmung für Kon", "", "2879385#0#41"));
       notifications.add(new ItemNotification("Document", "418", "2002-07-12 06:51:53", 88741256L, true, "http://www.ksta.de/koeln/frank-fussbroich-wegen-hehlerei-verurteilt 15187530 14399856.html", "Frank Fussbroich wegen Hehlerei verurteilt", "2875514", "Ein paar Paletten gestohlenen Kaffees haben den prominenten TV-Sohn auf die Anklagebank gebracht. Polizisten hatten den Deal mit dem Diebesgut beobachtet.", "", "0#2875514#31"));
       notifications.add(new ItemNotification("Document", "418", "2002-10-20 05:58:26", 140076324L, true, "http://www.ksta.de/region/jubilaeumsrennen-war-gleichzeitig-das-letzte 15189102 14349432.html", "Jubiläumsrennen war gleichzeitig das letzte", "2879993", "Zur Tradition geworden war der Drei-Flüsse-Lauf. Doch der 25. Lauf entlang Dhünn  Wupper und Rhein war der letzte.", "", "0#34#2879993"));
   
      /* notifications.add(new ItemNotification("Document", "418", "2001-11-03 06:27:24", 257971275L, true, "http://www.ksta.de/archiv/mehreinsatz-der-linie-19-geht-zu-lasten-anderer-bereiche 16592382 14531652.html?originalReferrer=https://www.google.de", "Mehreinsatz der Linie 19 geht zu Lasten anderer Bereiche", "0", "Als ich vor einigen Jahren aus meiner Heimatstadt Köln nach Hermülheim zog  war ich von der Idee einer direkten Straßenbahnverbindung in die Kölner City sehr angetan.", "", "0"));

          */


             notifications.add(new EventNotification("target", 133125713L, 0L, 1454281199725L, 2878799L, 504183L));
             notifications.add(new EventNotification("target", 101548478L, 37160546266831020L, 1454281202545L, 18850L, 654013L));
             notifications.add(new EventNotification("target", 89102479L, 4560325L, 1454281204334L, 2878799L, 504183L));
     
             notifications.add(new EventNotification(
                "click",
                97592046L,
                100L + (1 % 3),
                1714156300000L + (1 * 1000),
                10L + (1 % 2),
                5L + (1 % 2)
        ));


   /*    List<Long> recommendations = Arrays.asList(101L, 102L, 103L);
    public static String generateRandomIdRequest() {
        long currentTimeMillis = System.currentTimeMillis();
        Random random = new Random();
        int randomNumber = random.nextInt(1001);
        return "STR_" + currentTimeMillis + "_" + randomNumber;
    }

        String randomId = generateRandomIdRequest();
        RequestResponse response = new RequestResponse(
            777L, 3L, 456L, 1714156400000L, 15L, 3L
        );
        response.setRecs(recommendations);
        
   notifications.add(response);


     
       notifications.add(new Requestrelevant(
            777L, 3L, 456L, 1714156400000L, 15L, 3L,           List.of(101, 102, 103),
            List.of()
        )); */  
        return notifications;
    }








}
