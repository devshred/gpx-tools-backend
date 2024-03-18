package org.devshred.gpstools.domain.gps

import io.jenetics.jpx.geom.Geoid

data class GpsContainer(val name: String?, val wayPoints: List<WayPoint>, val track: Track?) {
    fun findWayPointOnTrackNearestTo(wayPoint: WayPoint): WayPoint {
        val gpxPoint = wayPoint.toGpxPoint()
        val nearestGpxPoint =
            track?.wayPoints?.stream()
                ?.map { it.toGpxPoint() }
                ?.reduce { result: io.jenetics.jpx.WayPoint, current: io.jenetics.jpx.WayPoint ->
                    if (Geoid.WGS84.distance(current, gpxPoint).toInt()
                        < Geoid.WGS84.distance(result, gpxPoint).toInt()
                    ) {
                        current
                    } else {
                        result
                    }
                }?.get()

        return WayPoint.fromGpxPoint(nearestGpxPoint!!)
    }

    fun findWayPointOnTrackNearestTo(
        wayPoint: WayPoint,
        tolerance: Int,
    ): WayPoint? {
        val nearestWayPoint = findWayPointOnTrackNearestTo(wayPoint)
        return if (Geoid.WGS84.distance(nearestWayPoint.toGpxPoint(), wayPoint.toGpxPoint()).toInt() > tolerance) {
            null
        } else {
            nearestWayPoint
        }
    }
}
