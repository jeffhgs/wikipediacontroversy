package wikiedits

import java.io.InputStream
import java.nio.file.{Path, StandardOpenOption}

object Decompress {
  def openLocal7z(path:Path) : InputStream = {
    val f7z = java.nio.channels.FileChannel.open(path, StandardOpenOption.READ)
    val arch = new org.apache.commons.compress.archivers.sevenz.SevenZFile(f7z)
    arch.getNextEntry
    //val ent = arch.getEntries.iterator().asScala.next()

    new InputStream {
      override def read(): Int = {
        arch.read()
      }

      override def read(b: Array[Byte], off: Int, len: Int): Int = {
        arch.read(b,off,len)
      }
    }
  }
}
