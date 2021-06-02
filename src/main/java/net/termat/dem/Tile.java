package net.termat.dem;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class Tile {

	@DatabaseField(generatedId=true)
    public long id;

	@DatabaseField
	public int z;

	@DatabaseField
	public int x;

	@DatabaseField
	public int y;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    public byte[] imageBytes;
}
