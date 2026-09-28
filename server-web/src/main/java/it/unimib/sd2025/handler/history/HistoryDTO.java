package it.unimib.sd2025.handler.history;

import java.util.Map;

import it.unimib.sd2025.model.Buono;

public class HistoryDTO {
    private Map<Integer, Buono> listaBuoni;

    public HistoryDTO(Map<Integer, Buono> listaBuoni) {
        this.listaBuoni = listaBuoni;
    }

    public Map<Integer, Buono> getListaBuoni() {
        return listaBuoni;
    }

    public void setListaBuoni(Map<Integer, Buono> listaBuoni) {
        this.listaBuoni = listaBuoni;
    }
}
