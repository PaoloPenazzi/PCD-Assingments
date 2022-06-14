package distributed

@main
def launcher(): Unit =
  val cityGrid = CityGrid(300, 300)
  cityGrid.createCityGrid(3, 3)
  
