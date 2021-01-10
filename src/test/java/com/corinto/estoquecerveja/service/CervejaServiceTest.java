package com.corinto.estoquecerveja.service;

import com.corinto.estoquecerveja.builder.CervejaDTOBuilder;
import com.corinto.estoquecerveja.dto.CervejaDTO;
import com.corinto.estoquecerveja.entity.Cerveja;
import com.corinto.estoquecerveja.exception.CervejaAlreadyRegisteredException;
import com.corinto.estoquecerveja.exception.CervejaNotFoundException;
import com.corinto.estoquecerveja.exception.CervejaStockExceededException;
import com.corinto.estoquecerveja.mapper.CervejaMapper;
import com.corinto.estoquecerveja.repository.CervejaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CervejaServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private CervejaRepository cervejaRepository;

    private CervejaMapper cervejaMapper = CervejaMapper.INSTANCE;

    @InjectMocks
    private CervejaService cervejaService;

    @Test
    void whenCervejaInformedThenItShouldBeCreated() throws CervejaAlreadyRegisteredException {
        // given
        CervejaDTO expectedCervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja expectedSavedCerveja = cervejaMapper.toModel(expectedCervejaDTO);

        // when
        when(cervejaRepository.findByNome(expectedCervejaDTO.getNome())).thenReturn(Optional.empty());
        when(cervejaRepository.save(expectedSavedCerveja)).thenReturn(expectedSavedCerveja);

        //then
        CervejaDTO createdBeerDTO = cervejaService.createCerveja(expectedCervejaDTO);

        assertThat(createdBeerDTO.getId(), is(equalTo(expectedCervejaDTO.getId())));
        assertThat(createdBeerDTO.getNome(), is(equalTo(expectedCervejaDTO.getNome())));
        assertThat(createdBeerDTO.getQuantidade(), is(equalTo(expectedCervejaDTO.getQuantidade())));
    }

    @Test
    void whenAlreadyRegisteredCervejaInformedThenAnExceptionShouldBeThrown() {
        // given
        CervejaDTO expectedCervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja duplicatedCerveja = cervejaMapper.toModel(expectedCervejaDTO);

        // when
        when(cervejaRepository.findByNome(expectedCervejaDTO.getNome())).thenReturn(Optional.of(duplicatedCerveja));

        // then
        assertThrows(CervejaAlreadyRegisteredException.class, () -> cervejaService.createCerveja(expectedCervejaDTO));
    }

    @Test
    void whenValidCervejaNameIsGivenThenReturnABeer() throws CervejaNotFoundException {
        // given
        CervejaDTO expectedFoundCervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja expectedFoundCerveja = cervejaMapper.toModel(expectedFoundCervejaDTO);

        // when
        when(cervejaRepository.findByNome(expectedFoundCerveja.getNome())).thenReturn(Optional.of(expectedFoundCerveja));

        // then
        CervejaDTO foundCervejaDTO = cervejaService.findByNome(expectedFoundCervejaDTO.getNome());

        assertThat(foundCervejaDTO, is(equalTo(expectedFoundCervejaDTO)));
    }

    @Test
    void whenNotRegisteredCervejaNameIsGivenThenThrowAnException() {
        // given
        CervejaDTO expectedFoundCervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        // when
        when(cervejaRepository.findByNome(expectedFoundCervejaDTO.getNome())).thenReturn(Optional.empty());

        // then
        assertThrows(CervejaNotFoundException.class, () -> cervejaService.findByNome(expectedFoundCervejaDTO.getNome()));
    }

    @Test
    void whenListCervejaIsCalledThenReturnAListOfBeers() {
        // given
        CervejaDTO expectedFoundCervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja expectedFoundCerveja = cervejaMapper.toModel(expectedFoundCervejaDTO);

        //when
        when(cervejaRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundCerveja));

        //then
        List<CervejaDTO> foundListBeersDTO = cervejaService.listAll();

        assertThat(foundListBeersDTO, is(not(empty())));
        assertThat(foundListBeersDTO.get(0), is(equalTo(expectedFoundCervejaDTO)));
    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
        //when
        when(cervejaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<CervejaDTO> foundListBeersDTO = cervejaService.listAll();

        assertThat(foundListBeersDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws CervejaNotFoundException{
        // given
        CervejaDTO expectedDeletedBeerDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja expectedDeletedBeer = cervejaMapper.toModel(expectedDeletedBeerDTO);

        // when
        when(cervejaRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
        doNothing().when(cervejaRepository).deleteById(expectedDeletedBeerDTO.getId());

        // then
        cervejaService.deleteById(expectedDeletedBeerDTO.getId());

        verify(cervejaRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
        verify(cervejaRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws CervejaNotFoundException, CervejaStockExceededException {
        //given
        CervejaDTO expectedBeerDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja expectedBeer = cervejaMapper.toModel(expectedBeerDTO);

        //when
        when(cervejaRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        when(cervejaRepository.save(expectedBeer)).thenReturn(expectedBeer);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedBeerDTO.getQuantidade() + quantityToIncrement;

        // then
        CervejaDTO incrementedBeerDTO = cervejaService.increment(expectedBeerDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDTO.getQuantidade()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedBeerDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        CervejaDTO expectedBeerDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja expectedBeer = cervejaMapper.toModel(expectedBeerDTO);

        when(cervejaRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 80;
        assertThrows(CervejaStockExceededException.class, () -> cervejaService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        CervejaDTO expectedCervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja expectedCerveja = cervejaMapper.toModel(expectedCervejaDTO);

        when(cervejaRepository.findById(expectedCervejaDTO.getId())).thenReturn(Optional.of(expectedCerveja));

        int quantityToIncrement = 45;
        assertThrows(CervejaStockExceededException.class, () -> cervejaService.increment(expectedCervejaDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(cervejaRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(CervejaNotFoundException.class, () -> cervejaService.increment(INVALID_BEER_ID, quantityToIncrement));
    }
//
//    @Test
//    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 5;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
//    }
//
//    @Test
//    void whenDecrementIsCalledToEmptyStockThenEmptyBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 10;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(0));
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//    }
//
//    @Test
//    void whenDecrementIsLowerThanZeroThenThrowException() {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//
//        int quantityToDecrement = 80;
//        assertThrows(BeerStockExceededException.class, () -> beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement));
//    }
//
//    @Test
//    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
//        int quantityToDecrement = 10;
//
//        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(INVALID_BEER_ID, quantityToDecrement));
//    }
}
