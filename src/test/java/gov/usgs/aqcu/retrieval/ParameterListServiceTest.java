package gov.usgs.aqcu.retrieval;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterMetadata;

import net.servicestack.client.IReturn;

@RunWith(SpringRunner.class)
public class ParameterListServiceTest {

	@MockBean
	private AquariusRetrievalService aquariusService;

	private ParameterListService service;

	private ParameterMetadata parameterMetadataA = new ParameterMetadata().setIdentifier("a").setUnitGroupIdentifier(ParameterListService.VOLUMETRIC_FLOW_UNIT_GROUP_VALUE);
	private ParameterMetadata parameterMetadataB = new ParameterMetadata().setIdentifier("b").setUnitGroupIdentifier("Not Volumetric");
	private ParameterMetadata parameterMetadataC = new ParameterMetadata().setIdentifier("c");

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		service = new ParameterListService(aquariusService);
		given(aquariusService.executePublishApiRequest(any(IReturn.class)))
				.willReturn(new ParameterListServiceResponse()
						.setParameters(new ArrayList<ParameterMetadata>(Arrays
								.asList(parameterMetadataA, parameterMetadataB, parameterMetadataC))));
	}

	@Test
	public void buildMapTest() {
		Map<String, ParameterMetadata> map = service.buildMap(Arrays.asList(parameterMetadataA, parameterMetadataB, parameterMetadataC));
		assertEquals(3, map.size());
		assertThat(map, IsMapContaining.hasEntry("a", parameterMetadataA));
		assertThat(map, IsMapContaining.hasEntry("b", parameterMetadataB));
		assertThat(map, IsMapContaining.hasEntry("c", parameterMetadataC));
	}

	@Test
	public void getParameterMetadata_happyTest() {
		Map<String, ParameterMetadata> map = service.getParameterMetadata();
		assertEquals(3, map.size());
		assertThat(map, IsMapContaining.hasEntry("a", parameterMetadataA));
		assertThat(map, IsMapContaining.hasEntry("b", parameterMetadataB));
		assertThat(map, IsMapContaining.hasEntry("c", parameterMetadataC));
	}

	@Test
	public void isVolumetricFlowTest() {
		Map<String, ParameterMetadata> map = service.getParameterMetadata();
		assertTrue(service.isVolumetricFlow(map, "a"));
		assertFalse(service.isVolumetricFlow(map, "b"));
		assertFalse(service.isVolumetricFlow(map, "c"));
	}

}
