package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.facsimile.Surface;
import es.ua.dlsi.im3.core.score.facsimile.Zone;
import es.ua.dlsi.im3.core.score.io.MEIScoreLayoutImporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MEIScoreLayoutImporterTest {

    @Test
    public void parse() {
        //TODO
        /*File file = TestFileUtils.getFile("/testdata/core/score/io/mei/system_page_break.mei");
        MEIScoreLayoutImporter meiScoreLayoutImporter = new MEIScoreLayoutImporter();
        ScoreLayout scoreLayout = meiScoreLayoutImporter.parse(file);
        assertTrue("Page Layout", scoreLayout instanceof PageLayout);
        PageLayout pageLayout = (PageLayout) scoreLayout;
        assertEquals("2 pages", 2, pageLayout.getPages().size());*/
    }

    @Test
    public void parsePartsPagesSystems() throws IM3Exception {
        //TODO
        File file = TestFileUtils.getFile("/testdata/core/score/io/mei/parts_pages_systems.mei");
        MEISongImporter meiSongImporter = new MEISongImporter(null);
        ScoreSong song = meiSongImporter.importSong(file);

        assertEquals("# parts", song.getParts().size(), 2);
        ScorePart sopranoPart = song.getPartWithName("Soprano");
        assertNotNull("Soprano", sopranoPart);
        ScorePart pianoPart = song.getPartWithName("Piano");
        assertNotNull("Piano", pianoPart);

        Facsimile facsimile = song.getFacsimile();
        assertNotNull("Facsimile", facsimile);

        assertEquals("3 surfaces", 3, facsimile.getSurfaceList().size());

        // first surface
        Surface surface = facsimile.getSurfaceList().get(0);
        assertEquals("1st surface id", "page_id_102", surface.getID());
        assertEquals("1st surface bounding box from x", 0, surface.getBoundingBox().getFromX(), 0.01);
        assertEquals("1st surface bounding box from y", 0, surface.getBoundingBox().getFromY(), 0.01);
        assertEquals("1st surface bounding box to x", 500, surface.getBoundingBox().getToX(), 0.01);
        assertEquals("1st surface bounding box to y", 900, surface.getBoundingBox().getToY(), 0.01);

        assertEquals("1st surface graphics size", 1, surface.getGraphicList().size());
        assertEquals("1st surface graphic target", "image304.jpg", surface.getGraphicList().get(0).getTarget());

        assertEquals("1st surface zones size", 5, surface.getZoneList().size());
        Zone zone = surface.getZoneList().get(0);
        assertEquals("1st surface 1st zone id", "region_id_3030", zone.getID());
        assertEquals("1st surface 1st zone bounding box from x", 20, zone.getBoundingBox().getFromX(), 0.01);
        assertEquals("1st surface 1st zone bounding box from y", 30, zone.getBoundingBox().getFromY(), 0.01);
        assertEquals("1st surface 1st zone bounding box to x", 203, zone.getBoundingBox().getToX(), 0.01);
        assertEquals("1st surface 1st zone bounding box to y", 119, zone.getBoundingBox().getToY(), 0.01);
        assertEquals("1st surface 1st zone type", "region", zone.getType());

        Zone zone2 = surface.getZoneList().get(1);
        assertEquals("1st surface 2nd zone id", "region_id_4001", zone2.getID());
        assertEquals("1st surface 2nd zone bounding box from x", 1, zone2.getBoundingBox().getFromX(), 0.01);
        assertEquals("1st surface 2nd zone bounding box from y", 4, zone2.getBoundingBox().getFromY(), 0.01);
        assertEquals("1st surface 2nd zone bounding box to x", 1000, zone2.getBoundingBox().getToX(), 0.01);
        assertEquals("1st surface 2nd zone bounding box to y", 150, zone2.getBoundingBox().getToY(), 0.01);
        assertEquals("1st surface 2nd zone type", "agnostic_symbol", zone2.getType());

        Zone zoneN = surface.getZoneList().get(surface.getZoneList().size()-1);
        assertEquals("1st surface last zone id", "symbol_id_5001", zoneN.getID());
        assertEquals("1st surface last zone bounding box from x", 30, zoneN.getBoundingBox().getFromX(), 0.01);
        assertEquals("1st surface last zone bounding box from y", 33, zoneN.getBoundingBox().getFromY(), 0.01);
        assertEquals("1st surface last zone bounding box to x", 60, zoneN.getBoundingBox().getToX(), 0.01);
        assertEquals("1st surface last zone bounding box to y", 90, zoneN.getBoundingBox().getToY(), 0.01);
        assertEquals("1st surface last zone type", "region", zoneN.getType());

        // 2nd surface
        assertEquals("2nd surface graphic target", "image304.jpg", facsimile.getSurfaceList().get(1).getGraphicList().get(0).getTarget());

        Zone surface2Zone = facsimile.getSurfaceList().get(1).getZoneList().get(0);
        assertEquals("2nd surface 1st zone id", "region_id_3033", surface2Zone.getID());
        assertEquals("2nd surface 1st zone bounding box from x", 23, surface2Zone.getBoundingBox().getFromX(), 0.01);
        assertEquals("2nd surface 1st zone bounding box from y", 31, surface2Zone.getBoundingBox().getFromY(), 0.01);
        assertEquals("2nd surface 1st zone bounding box to x", 201, surface2Zone.getBoundingBox().getToX(), 0.01);
        assertEquals("2nd surface 1st zone bounding box to y", 131, surface2Zone.getBoundingBox().getToY(), 0.01);
        assertEquals("2nd surface 1st zone type", "region", surface2Zone.getType());

        // 3rd surface
        assertEquals("3rd surface graphic target", "image320.jpg", facsimile.getSurfaceList().get(2).getGraphicList().get(0).getTarget());


        // 1st part
        assertEquals("1 soprano staff", 1, sopranoPart.getStaves().size());
        Staff sopranoStaff = sopranoPart.getStaves().get(0);
        assertEquals("Pages", 2, sopranoStaff.getPageBreaks().size());
        PartPageBreak sopranoPB1 = sopranoStaff.getPageBreaks().get(Time.TIME_ZERO);
        assertEquals("PB1 soprano id", "#page_id_102", sopranoPB1.getFacsimileElementID());

        PartPageBreak sopranoPB2 = sopranoStaff.getPageBreaks().get(Figures.WHOLE.getDuration().multiply(2));
        assertEquals("PB2 soprano id", "#page_id_103", sopranoPB2.getFacsimileElementID());

        assertEquals("Systems", 3, sopranoStaff.getSystemBreaks().size());
        assertEquals("SB1 soprano id", "#region_id_3030", sopranoStaff.getSystemBreaks().get(Time.TIME_ZERO).getFacsimileElementID());
        assertEquals("SB2 soprano id", "#region_id_3031", sopranoStaff.getSystemBreaks().get(Figures.WHOLE.getDuration()).getFacsimileElementID());
        assertEquals("SB3 soprano id", "#region_id_3033", sopranoStaff.getSystemBreaks().get(Figures.WHOLE.getDuration().multiply(2)).getFacsimileElementID());

        //TODO facs reference from note
    }
}
