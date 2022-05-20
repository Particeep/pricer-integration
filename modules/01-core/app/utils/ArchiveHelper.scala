package utils

import akka.util.ByteString

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }
import java.util.zip.ZipInputStream
import scala.annotation.tailrec
/*
  utility inspired by alkappa project
  java code version there : https://github.com/akka/alpakka/blob/50505de861cb789b48b21fa8a89e2856fc9249a9/file/src/test/java/docs/javadsl/ArchiveHelper.java
 */
object ArchiveHelper {

  def unzip(zipArchive: ByteString): Map[String, ByteString] = {
    val zis    = new ZipInputStream(new ByteArrayInputStream(zipArchive.toArray))
    val result = lookForEntry(zis)
    zis.close()
    result
  }

  @tailrec
  private[this] def lookForEntry(
    zis:    ZipInputStream,
    result: Map[String, ByteString] = Map.empty
  ): Map[String, ByteString] = {
    Option(zis.getNextEntry) match {
      case Some(entry) =>
        val dest = new ByteArrayOutputStream
        writeUntilOver(zis, dest)
        dest.flush()
        dest.close()
        zis.closeEntry()
        lookForEntry(zis, result + (entry.getName -> ByteString.fromArray(dest.toByteArray)))
      case None        => result
    }
  }

  @tailrec
  private[this] def writeUntilOver(zis: ZipInputStream, dest: ByteArrayOutputStream): ByteArrayOutputStream = {
    val data = new Array[Byte](1024)

    zis.read(data, 0, 1024) match {
      case count if count != -1 =>
        dest.write(data, 0, count)
        writeUntilOver(zis, dest)
      case _                    => dest
    }
  }
}
