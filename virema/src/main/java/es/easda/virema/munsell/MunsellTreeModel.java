package es.easda.virema.munsell;

import java.io.*;
import java.util.*;

public class MunsellTreeModel {
    private static final String DELIMITER = ";";
    private static String [] H_LIST =
            {"2.5R",  "5R",  "7.5R",  "10R",
            "2.5YR", "5YR", "7.5YR", "10YR",
            "2.5Y",  "5Y",  "7.5Y",  "10Y",
            "2.5GY", "5GY", "7.5GY", "10GY",
            "2.5G",  "5G",  "7.5G",  "10G",
            "2.5BG", "5BG", "7.5BG", "10BG",
            "2.5B",  "5B",  "7.5B",  "10B",
            "2.5PB", "5PB", "7.5PB", "10PB",
            "2.5P",  "5P",  "7.5P",  "10P",
            "2.5RP", "5RP", "7.5RP", "10RP"
            };
    private HashMap<String, Double> hNameDegreeMap;

    HashMap<String, List<MunsellColor>> colors;

    public MunsellTreeModel() throws Exception {
        buildHDegrees();
        loadData();
    }

    public String[] getHues() {
        return H_LIST;
    }

    private void buildHDegrees() {
        double degrees = 0;
        double inc = 360.0 / (double)H_LIST.length;
        int i=0;
        hNameDegreeMap = new HashMap<>();
        for (String hName: H_LIST) {
            hNameDegreeMap.put(hName, degrees);
            System.out.println(i + " " + hName + " " + degrees + "º");
            degrees += inc;
            i++;
        }
    }

    private void loadData() throws Exception {
        colors = new HashMap<>();
        for (String hn: H_LIST) {
            colors.put(hn, new ArrayList<>());
        }

        int maxC = 0;
        int maxV = 0;

        List<List<String>> csvData = loadCSV(this.getClass().getResourceAsStream("/real_sRGB.csv"), DELIMITER);
        for (int i=0; i<csvData.size(); i++) {
            List<String> line = csvData.get(i);
            if (i == 0) {
                if (line.size() != 19) {
                    throw new Exception("Expected 19 columns, and found " + line.size());
                }
                if (!Objects.equals("h", line.get(1))) {
                    throw new Exception("Expecting h at the column B");
                }
                if (!Objects.equals("V", line.get(2))) {
                    throw new Exception("Expecting V at the column C");
                }
                if (!Objects.equals("C", line.get(3))) {
                    throw new Exception("Expecting h at the column D");
                }
                if (!Objects.equals("R", line.get(13))) {
                    throw new Exception("Expecting h at the column N");
                }
                if (!Objects.equals("G", line.get(14))) {
                    throw new Exception("Expecting h at the column O");
                }
                if (!Objects.equals("B", line.get(15))) {
                    throw new Exception("Expecting h at the column P");
                }
                if (!Objects.equals("dR", line.get(16))) {
                    throw new Exception("Expecting h at the column Q");
                }
                if (!Objects.equals("dG", line.get(17))) {
                    throw new Exception("Expecting h at the column R");
                }
                if (!Objects.equals("dB", line.get(18))) {
                    throw new Exception("Expecting h at the column S");
                }
            } else {
                MunsellColor munsellColor = new MunsellColor();
                munsellColor.sethName(line.get(1));
                munsellColor.sethDegrees(hName2Degrees(line.get(1)));
                munsellColor.setV(Integer.parseInt(line.get(2)));
                munsellColor.setC(Integer.parseInt(line.get(3)));
                munsellColor.setR(Double.parseDouble(line.get(13).replace(',', '.')));
                munsellColor.setG(Double.parseDouble(line.get(14).replace(',', '.')));
                munsellColor.setB(Double.parseDouble(line.get(15).replace(',', '.')));
                munsellColor.setDr(Integer.parseInt(line.get(16)));
                munsellColor.setDg(Integer.parseInt(line.get(17)));
                munsellColor.setDb(Integer.parseInt(line.get(18)));
                colors.get(munsellColor.gethName()).add(munsellColor);

                maxC = Math.max(maxC, munsellColor.getC());
                maxV = Math.max(maxV, munsellColor.getV());
            }
        }
        System.out.println("Max C = " + maxC);
        System.out.println("Max V = " + maxV);
    }

    private double hName2Degrees(String s) throws Exception {
        Double result = this.hNameDegreeMap.get(s);
        if (result == null) {
            throw new Exception("Cannot find the degree equivalence for '" + s + "'");
        }
        return result;
    }

    private List<List<String>> loadCSV(InputStream inputStream, String separator) throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(separator);
                records.add(Arrays.asList(values));
            }
        }
        return records;
    }

    public List<MunsellColor> getColors(String hname) {
        return colors.get(hname);
    }

    /**
     * It finds the closest color to the specified rgb
     * @return
     */
    public MunsellColor findClosest(int r, int g, int b) {
        MunsellColor best = null;
        double bestDiff = Double.MAX_VALUE;

        for (List<MunsellColor> munsellColorList: colors.values()) {
            for (MunsellColor munsellColor : munsellColorList) {
                double diff = munsellColor.getDistance(r, g, b);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    best = munsellColor;
                }
            }
        }
        return best;
    }
}
