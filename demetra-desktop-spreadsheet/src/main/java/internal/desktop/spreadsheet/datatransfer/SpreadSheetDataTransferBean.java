package internal.desktop.spreadsheet.datatransfer;

import demetra.tsprovider.grid.GridReader;
import demetra.tsprovider.grid.GridWriter;

@lombok.Data
class SpreadSheetDataTransferBean {

    private boolean importTs = true;
    private GridReader tsReader = GridReader.DEFAULT;

    private boolean exportTs = true;
    private GridWriter tsWriter = GridWriter.DEFAULT;
    
    private boolean importMatrix = true;
    private boolean exportMatrix = true;

    private boolean importTable = true;
    private boolean exportTable = true;
}
