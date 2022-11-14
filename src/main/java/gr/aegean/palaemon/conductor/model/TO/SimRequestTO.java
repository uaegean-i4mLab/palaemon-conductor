package gr.aegean.palaemon.conductor.model.TO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimRequestTO {
 private String noOfData;
 private String positionError;
 private String pathErrorPrcntg;
 private  int deck;
 private int oxygenProblemPrnctg;
 private int heartProblemPrnctg;
    /*
{
    "noOfData" : "100",
    "positionError" : "0.5",
    "pathErrorPrcntg" : "0",
    "deck" : 7,
    "oxygenProblemPrnctg" : 1,
    "heartProblemPrnctg" : 1
}
 */

}
